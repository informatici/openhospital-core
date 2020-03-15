package org.isf.medicalstock.service;

import java.util.GregorianCalendar;

public class QueryParameterContainer {
	private final FromTo movementFromTo;
	private final FromTo lotPrepFromTo;
	private final FromTo lotDueFromTo;
	private final String wardId;
	private final String movementType;
	private final String medicalType;
	private final Integer medicalCode;
	private final String medicalDescription;
	private String lotCode;

	private QueryParameterContainer(GregorianCalendar movFrom, GregorianCalendar movTo, GregorianCalendar lotPrepFrom,
									GregorianCalendar lotPrepTo, GregorianCalendar lotDueFrom, GregorianCalendar lotDueTo,
									String wardId, String movementType, String medicalType, Integer medicalCode,
									String medicalDescription, String lotCode) {
		this.movementFromTo = new FromTo(movFrom, movTo);
		this.lotPrepFromTo = new FromTo(lotPrepFrom, lotPrepTo);
		this.lotDueFromTo = new FromTo(lotDueFrom, lotDueTo);
		this.wardId = wardId;
		this.movementType = movementType;
		this.medicalType = medicalType;
		this.medicalCode = medicalCode;
		this.medicalDescription = medicalDescription;
		this.lotCode = lotCode;
	}

	public GregorianCalendar getMovementfrom() {
		return movementFromTo.from;
	}

	public GregorianCalendar getMovementTo() {
		return movementFromTo.to;
	}

	public GregorianCalendar getLotPrepFrom() {
		return lotPrepFromTo.from;
	}

	public GregorianCalendar getLotPrepTo() {
		return lotPrepFromTo.to;
	}

	public GregorianCalendar getLotDueFrom() {
		return lotDueFromTo.from;
	}

	public GregorianCalendar getLotDueTo() {
		return lotDueFromTo.to;
	}

	public String getWardId() {
		return wardId;
	}

	public String getMovementType() {
		return movementType;
	}

	public String getMedicalType() {
		return medicalType;
	}

	public Integer getMedicalCode() {
		return medicalCode;
	}

	public String getLotCode() {
		return lotCode;
	}

	public String getMedicalDescription() {
		return medicalDescription;
	}

	private static class FromTo {
		private final GregorianCalendar from;
		private final GregorianCalendar to;

		private FromTo(final GregorianCalendar from, final GregorianCalendar to) {
			this.from = from;
			this.to = to;
		}
	}

	public static class QueryParameterContainerBuilder {
		private FromTo movementfromTo;
		private FromTo lotPrepFromTo;
		private FromTo lotDueFromTo;
		private String wardId;
		private String movementType;
		private String medicalType;
		private Integer medicalCode;
		private String medicalDescription;
		private String lotCode;

		public static QueryParameterContainerBuilder instance() {
			return new QueryParameterContainerBuilder();
		}

		public QueryParameterContainerBuilder withMovementFromTo(final GregorianCalendar movFrom, final GregorianCalendar movTo) {
			this.movementfromTo = new FromTo(movFrom, movTo);
			return this;
		}

		public QueryParameterContainerBuilder withLotPrepFromTo(final GregorianCalendar lotPrepFrom, final GregorianCalendar lotPrepTo) {
			this.lotPrepFromTo = new FromTo(lotPrepFrom, lotPrepTo);
			return this;
		}

		public QueryParameterContainerBuilder withLotDueFromTo(final GregorianCalendar lotDueFrom, final GregorianCalendar lotDueTo) {
			this.lotDueFromTo = new FromTo(lotDueFrom, lotDueTo);
			return this;
		}

		public QueryParameterContainerBuilder withWardId(String wardId) {
			this.wardId = wardId;
			return this;
		}

		public QueryParameterContainerBuilder withMovementType(String movementType) {
			this.movementType = movementType;
			return this;
		}

		public QueryParameterContainerBuilder withMedicalType(String medicalType) {
			this.medicalType = medicalType;
			return this;
		}

		public QueryParameterContainerBuilder withMedicalCode(Integer medicalCode) {
			this.medicalCode = medicalCode;
			return this;
		}

		public QueryParameterContainerBuilder withMedicalDescription(String medicalDescription) {
			this.medicalDescription = medicalDescription;
			return this;
		}

		public QueryParameterContainerBuilder withLotCode(String lotCode) {
			this.lotCode = lotCode;
			return this;
		}

		public QueryParameterContainer build() {
			return new QueryParameterContainer(movementfromTo != null ? movementfromTo.from : null,
					movementfromTo != null ? movementfromTo.to : null,
					lotPrepFromTo != null ? lotPrepFromTo.from : null,
					lotPrepFromTo != null ? lotPrepFromTo.to : null,
					lotDueFromTo != null ? lotDueFromTo.from : null,
					lotDueFromTo != null ? lotDueFromTo.to : null,
					wardId,
					movementType,
					medicalType,
					medicalCode,
					medicalDescription,
					lotCode
					);
		}

	}
}
