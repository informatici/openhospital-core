package org.isf.conditioning.manager;

import org.isf.conditioning.model.Conditioning;
import org.isf.conditioning.service.ConditioningOperations;
import org.isf.generaldata.MessageBundle;
import org.isf.menu.model.User;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ConditioningBrowserManager {
	private final ConditioningOperations conditioningOperations;

	public ConditioningBrowserManager(ConditioningOperations conditioningOperations) {
		this.conditioningOperations = conditioningOperations;
	}

	public Conditioning getConditioning(int id) throws OHServiceException {
		return conditioningOperations.getConditioning(id);
	}

	public Conditioning updateConditioning(Conditioning conditioning) throws OHServiceException {
		validateConditioning(conditioning);
		return conditioningOperations.updateConditioning(conditioning);
	}


	/**
	 * Valide un objet Conditioning avant enregistrement.
	 * @param conditioning l'objet à valider
	 * @throws OHServiceException si une règle n'est pas respectée
	 */
	public void validateConditioning(Conditioning conditioning) throws OHServiceException {
		List<OHExceptionMessage> errors = new ArrayList<>();

		if (conditioning == null) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("Conditioning ne peut pas être null.")));
		}

		User performer = conditioning.getPerformBy();
		if (performer == null) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("Le champ performBy est obligatoire.")));
		}

		if (conditioning.getMceDuree() != null && conditioning.getMceDuree() < 0) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("mceDuree ne peut pas être négatif.")));
		}
		if (conditioning.getVentilationDuree() != null && conditioning.getVentilationDuree() < 0) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("ventilationDuree ne peut pas être négatif.")));
		}

		if (conditioning.getOxygeneDebit() != null && conditioning.getOxygeneDebit() < 0) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("oxygeneDebit ne peut pas être négatif.")));
		}
		if (conditioning.getSgVolume() != null && conditioning.getSgVolume() < 0) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("sgVolume ne peut pas être négatif.")));
		}
		if (conditioning.getDiazepamDose() != null && conditioning.getDiazepamDose() < 0) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("diazepamDose ne peut pas être négatif.")));
		}
		if (conditioning.getBolusSsVolume() != null && conditioning.getBolusSsVolume() < 0) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("bolusSsVolume ne peut pas être négatif.")));
		}

		if (conditioning.getPerformAt() == null) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("performAt est obligatoire.")));
		}
		if (conditioning.getPerformAt().isAfter(java.time.LocalDateTime.now())) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("performAt ne peut pas être dans le futur.")));
		}

		if (conditioning.getSngNumero() != null && conditioning.getSngNumero().length() > 50) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("sngNumero trop long (max 50 caractères).")));
		}
		if (conditioning.getOthers() != null && conditioning.getOthers().length() > 255) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("others trop long (max 255 caractères).")));
		}
	}
}
