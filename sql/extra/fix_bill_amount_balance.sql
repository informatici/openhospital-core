-- =============================================================================
-- fix_bill_amount_balance.sql
--
-- Recomputes BLL_AMOUNT and BLL_BALANCE for every non-deleted OH_BILLS row
-- from the authoritative child tables (OH_BILLITEMS, OH_BILLPAYMENTS).
--
-- Background
-- ----------
-- Prior to the fix in BillBrowserManager.recalculateTotals(), the GUI could
-- persist stale BLL_AMOUNT / BLL_BALANCE values supplied by the front-end
-- instead of computing them from OH_BILLITEMS. This script corrects
-- historical data in place.
--
-- Business rules applied (mirrors recalculateTotals())
-- -----------------------------------------------------
--   BLL_AMOUNT  : sum of (BLI_ITEM_AMOUNT * BLI_QTY) for groups whose net
--                 quantity > 0 AND unit price > 0.
--                 Items are grouped by (BLI_ID_PRICE | BLI_ITEM_DESC) to
--                 handle write-off (negative-qty cancellation) rows correctly.
--   BLL_BALANCE : gross total of ALL groups (positive + negative values) minus
--                 total payments recorded in OH_BILLPAYMENTS.
--   Rounding    : both values rounded to 2 decimal places.
--
-- Status values in this schema
-- ----------------------------
--   'O'  Open
--   'C'  Closed
--   'D'  Deleted  <- skipped by this script
--
-- Usage
-- -----
--   mysql -u <user> -p <database> < fix_bill_amount_balance.sql
--
-- The script is idempotent: running it multiple times produces the same result.
-- A transaction wraps everything so you can inspect the diff report and then
-- decide to COMMIT or ROLLBACK.
-- =============================================================================

START TRANSACTION;

-- ---------------------------------------------------------------------------
-- Step 1 – compute net amounts per bill from OH_BILLITEMS.
-- ---------------------------------------------------------------------------
CREATE TEMPORARY TABLE IF NOT EXISTS _bill_item_totals AS
SELECT
    grp.BLI_ID_BILL,
    ROUND(SUM(grp.net_value), 2)                                          AS big_total,
    ROUND(SUM(CASE
                  WHEN grp.unit_price > 0 AND grp.net_qty > 0
                  THEN grp.net_value
                  ELSE 0
              END), 2)                                                     AS amount
FROM (
    SELECT
        BLI_ID_BILL,
        BLI_ITEM_AMOUNT                         AS unit_price,
        SUM(BLI_QTY)                            AS net_qty,
        BLI_ITEM_AMOUNT * SUM(BLI_QTY)         AS net_value
    FROM OH_BILLITEMS
    GROUP BY BLI_ID_BILL, BLI_ID_PRICE, BLI_ITEM_DESC, BLI_ITEM_AMOUNT
) grp
GROUP BY grp.BLI_ID_BILL;

-- ---------------------------------------------------------------------------
-- Step 2 – compute total payments per bill from OH_BILLPAYMENTS.
-- ---------------------------------------------------------------------------
CREATE TEMPORARY TABLE IF NOT EXISTS _bill_payment_totals AS
SELECT
    BLP_ID_BILL,
    ROUND(SUM(BLP_AMOUNT), 2) AS total_paid
FROM OH_BILLPAYMENTS
GROUP BY BLP_ID_BILL;

-- ---------------------------------------------------------------------------
-- Step 3 – snapshot old values BEFORE the update so the diff report is
--           meaningful. Bills whose stored values already match the
--           recomputed ones will not appear in Step 4.
-- ---------------------------------------------------------------------------
CREATE TEMPORARY TABLE IF NOT EXISTS _bill_old_values AS
SELECT
    b.BLL_ID,
    b.BLL_STATUS,
    b.BLL_AMOUNT                                                          AS old_amount,
    b.BLL_BALANCE                                                         AS old_balance,
    ROUND(COALESCE(it.amount,    0), 2)                                   AS new_amount,
    ROUND(COALESCE(it.big_total, 0) - COALESCE(pt.total_paid, 0), 2)     AS new_balance
FROM OH_BILLS b
LEFT JOIN _bill_item_totals   it ON it.BLI_ID_BILL  = b.BLL_ID
LEFT JOIN _bill_payment_totals pt ON pt.BLP_ID_BILL = b.BLL_ID
WHERE b.BLL_STATUS <> 'D';

-- ---------------------------------------------------------------------------
-- Step 4 – apply the fix.
-- ---------------------------------------------------------------------------
UPDATE OH_BILLS b
LEFT JOIN _bill_item_totals   it ON it.BLI_ID_BILL  = b.BLL_ID
LEFT JOIN _bill_payment_totals pt ON pt.BLP_ID_BILL = b.BLL_ID
SET
    b.BLL_AMOUNT  = ROUND(COALESCE(it.amount,    0), 2),
    b.BLL_BALANCE = ROUND(COALESCE(it.big_total, 0) - COALESCE(pt.total_paid, 0), 2)
WHERE b.BLL_STATUS <> 'D';

-- ---------------------------------------------------------------------------
-- Step 5 – diff report: only bills whose stored values actually changed.
--           Review this output before deciding to COMMIT or ROLLBACK.
-- ---------------------------------------------------------------------------
SELECT
    o.BLL_ID,
    o.BLL_STATUS,
    o.old_amount,
    o.new_amount,
    ROUND(o.new_amount  - o.old_amount,  2) AS amount_delta,
    o.old_balance,
    o.new_balance,
    ROUND(o.new_balance - o.old_balance, 2) AS balance_delta
FROM _bill_old_values o
WHERE ABS(o.new_amount  - o.old_amount)  > 0.001
   OR ABS(o.new_balance - o.old_balance) > 0.001
ORDER BY o.BLL_ID;

DROP TEMPORARY TABLE IF EXISTS _bill_item_totals;
DROP TEMPORARY TABLE IF EXISTS _bill_payment_totals;
DROP TEMPORARY TABLE IF EXISTS _bill_old_values;

-- After reviewing the diff report above:
--   COMMIT;    -- to apply the changes
--   ROLLBACK;  -- to discard and inspect further
ROLLBACK;
