-- OP-1428: persist the lot's hospital-wide remaining quantity in a new LT_QTY column, as a mirror of the overall
-- quantity computed at runtime: the signed sum of the main-store movements (charges minus discharges) plus the
-- ward stock (in quantity minus out quantity). The backfill below initializes the column with exactly that
-- definition; from then on the application keeps it aligned by applying a delta whenever a movement changes it.
--
-- The sign of a main-store movement deliberately follows the movement type PREFIX (MMVT_TYPE LIKE '+%'), matching
-- the JPQL live queries (getMainStoreQuantity / getMainStoreQuantities in LotIoOperationRepository): the backfill
-- and the runtime deltas must agree on the same charge/discharge classification, or LT_QTY would drift away from
-- the computed quantity.

ALTER TABLE OH_MEDICALDSRLOT ADD COLUMN LT_QTY DECIMAL(19,2) NOT NULL DEFAULT 0 AFTER LT_COST;

UPDATE OH_MEDICALDSRLOT SET LT_QTY =
	COALESCE((SELECT SUM(CASE WHEN MMVT_TYPE LIKE '+%' THEN MMV_QTY ELSE -MMV_QTY END)
		FROM OH_MEDICALDSRSTOCKMOV JOIN OH_MEDICALDSRSTOCKMOVTYPE ON MMV_MMVT_ID_A = MMVT_ID_A
		WHERE MMV_LT_ID_A = LT_ID_A), 0)
	+ COALESCE((SELECT SUM(MDSRWRD_IN_QTI - MDSRWRD_OUT_QTI)
		FROM OH_MEDICALDSRWARD
		WHERE MDSRWRD_LT_ID_A = LT_ID_A), 0);
