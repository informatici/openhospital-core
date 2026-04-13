# sql/extra — Manual maintenance scripts

Scripts in this directory are **not applied automatically** during installation
or upgrade.  Each one targets a specific data-quality or administrative task
and must be run manually by a database administrator after carefully reviewing
its output.

---

## Scripts

### `fix_bill_amount_balance.sql`

**When to use**  
Run this script once when upgrading to **1.15.1** (or any later release that
includes the `BillBrowserManager.recalculateTotals()` fix) on a database that
was previously populated with Open Hospital 1.15.0 or earlier.

**Problem it solves**  
Prior to 1.15.1, the billing GUI could save the value of `BLL_AMOUNT` and
`BLL_BALANCE` as supplied by the front-end form rather than recomputing them
from the actual item lines in `OH_BILLITEMS`.  As a result:

- Editing or adding a payment to an existing bill could leave `BLL_AMOUNT`
  stale (e.g. zero, or carrying a value from a previous session).
- Saving a bill with an empty item list on the update path triggered
  `deleteWhereId()` internally, silently wiping all `OH_BILLITEMS` rows while
  the header remained with its old, now-orphaned amounts.

**What the script does**  
Recomputes `BLL_AMOUNT` and `BLL_BALANCE` for every non-deleted bill
(`BLL_STATUS <> 'D'`) using the same rules as `recalculateTotals()`:

| Field | Rule |
|---|---|
| `BLL_AMOUNT` | Sum of `BLI_ITEM_AMOUNT × BLI_QTY` for groups where net qty > 0 **and** unit price > 0 (service lines only; write-off and discount lines excluded). |
| `BLL_BALANCE` | Gross total of all item groups minus total payments in `OH_BILLPAYMENTS`, rounded to 2 decimal places. |

Items with the same `(BLI_ID_PRICE, BLI_ITEM_DESC)` key are aggregated before
applying the rules, so write-off rows (negative quantity) correctly cancel the
corresponding positive-quantity lines.

Bills with status `'D'` (Deleted) are skipped entirely.

**How to run**

1. **Take a database backup** before running any DML script on production data.

2. Open a MySQL client session and run the script in dry-run mode (the default —
   the script ends with `ROLLBACK`):

   ```bash
   mysql -u <user> -p <database> < fix_bill_amount_balance.sql
   ```

3. Examine the diff report printed by Step 5.  It lists only the bills whose
   stored values differ from the recomputed ones:

   | Column | Meaning |
   |---|---|
   | `BLL_ID` | Bill identifier |
   | `BLL_STATUS` | `O` = Open, `C` = Closed |
   | `old_amount` / `new_amount` | Stored vs recomputed `BLL_AMOUNT` |
   | `amount_delta` | `new_amount − old_amount` |
   | `old_balance` / `new_balance` | Stored vs recomputed `BLL_BALANCE` |
   | `balance_delta` | `new_balance − old_balance` |

   If the result set is **empty**, your database is already consistent and no
   changes are needed.

4. If the diff looks correct, open the script in a text editor, change the
   last line from `ROLLBACK;` to `COMMIT;`, and run it again to apply the
   changes permanently:

   ```bash
   mysql -u <user> -p <database> < fix_bill_amount_balance.sql
   ```

   Alternatively, run the steps interactively inside a single MySQL session so
   you can commit or roll back without editing the file:

   ```sql
   SOURCE /path/to/fix_bill_amount_balance.sql   -- ends with ROLLBACK by default
   -- review the output, then:
   START TRANSACTION;
   -- re-run steps 1-4 manually, then:
   COMMIT;
   ```

**Idempotency**  
The script is safe to run multiple times.  After a successful `COMMIT` the diff
report will return zero rows on subsequent runs.

**Caution**  
Some installations may carry intentional manual adjustments to `BLL_AMOUNT` or
`BLL_BALANCE` (e.g. off-system discounts entered directly in the database).
Review the diff report carefully before committing, and involve your finance or
audit team if any unexpected deltas appear.

---

### `extract_patient_profile_photo_from_db.sql`

Extracts patient photos stored as BLOBs in the database to the filesystem.
See the comments inside the script for usage instructions.

---

### `extract_dicom_data_from_db.sql`

Extracts DICOM data stored as BLOBs in the database to the filesystem.
See the comments inside the script for usage instructions.

---

### `reset_admin_password_strong.sql`

Resets the `admin` user password to a strong default.
See the comments inside the script for usage instructions.
