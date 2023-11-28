package org.isf.examination.manager;

import org.isf.generaldata.MessageBundle;
import org.isf.utils.validator.DefaultSorter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DescriptionUtil {
	private HashMap<String, String> diuresisDescriptionHashMap;
	private HashMap<String, String> bowelDescriptionHashMap;

	public DescriptionUtil() {
	}

	public DescriptionUtil(HashMap<String, String> diuresisDescriptionHashMap, HashMap<String, String> bowelDescriptionHashMap) {
		this.diuresisDescriptionHashMap = diuresisDescriptionHashMap;
		this.bowelDescriptionHashMap = bowelDescriptionHashMap;
	}

	public HashMap<String, String> getDiuresisDescriptionHashMap() {
		return diuresisDescriptionHashMap;
	}

	public void setDiuresisDescriptionHashMap(HashMap<String, String> diuresisDescriptionHashMap) {
		this.diuresisDescriptionHashMap = diuresisDescriptionHashMap;
	}

	public HashMap<String, String> getBowelDescriptionHashMap() {
		return bowelDescriptionHashMap;
	}

	public void setBowelDescriptionHashMap(HashMap<String, String> bowelDescriptionHashMap) {
		this.bowelDescriptionHashMap = bowelDescriptionHashMap;
	}

	public String getBMIdescription(double bmi) {
		if (bmi < 16.5) {
			return MessageBundle.getMessage("angal.examination.bmi.severeunderweight.txt");
		}
		if (bmi >= 16.5 && bmi < 18.5) {
			return MessageBundle.getMessage("angal.examination.bmi.underweight.txt");
		}
		if (bmi >= 18.5 && bmi < 24.5) {
			return MessageBundle.getMessage("angal.examination.bmi.normalweight.txt");
		}
		if (bmi >= 24.5 && bmi < 30.0) {
			return MessageBundle.getMessage("angal.examination.bmi.overweight.txt");
		}
		if (bmi >= 30.0 && bmi < 35.0) {
			return MessageBundle.getMessage("angal.examination.bmi.obesityclassilight.txt");
		}
		if (bmi >= 35.0 && bmi < 40.0) {
			return MessageBundle.getMessage("angal.examination.bmi.obesityclassiimedium.txt");
		}
		return MessageBundle.getMessage("angal.examination.bmi.obesityclassiiisevere.txt");
	}

	public void buildDiuresisDescriptionHashMap() {
		diuresisDescriptionHashMap = new HashMap<>(8);
		diuresisDescriptionHashMap.put("physiological", MessageBundle.getMessage("angal.examination.diuresis.physiological.txt"));
		diuresisDescriptionHashMap.put("oliguria", MessageBundle.getMessage("angal.examination.diuresis.oliguria.txt"));
		diuresisDescriptionHashMap.put("anuria", MessageBundle.getMessage("angal.examination.diuresis.anuria.txt"));
		diuresisDescriptionHashMap.put("frequent", MessageBundle.getMessage("angal.examination.diuresis.frequent.txt"));
		diuresisDescriptionHashMap.put("nocturia", MessageBundle.getMessage("angal.examination.diuresis.nocturia.txt"));
		diuresisDescriptionHashMap.put("stranguria", MessageBundle.getMessage("angal.examination.diuresis.stranguria.txt"));
		diuresisDescriptionHashMap.put("hematuria", MessageBundle.getMessage("angal.examination.diuresis.hematuria.txt"));
		diuresisDescriptionHashMap.put("pyuria", MessageBundle.getMessage("angal.examination.diuresis.pyuria.txt"));
	}

	public void buildBowelDescriptionHashMap() {
		bowelDescriptionHashMap = new HashMap<>(4);
		bowelDescriptionHashMap.put("regular", MessageBundle.getMessage("angal.examination.bowel.regular.txt"));
		bowelDescriptionHashMap.put("irregular", MessageBundle.getMessage("angal.examination.bowel.irregular.txt"));
		bowelDescriptionHashMap.put("constipation", MessageBundle.getMessage("angal.examination.bowel.constipation.txt"));
		bowelDescriptionHashMap.put("diarrheal", MessageBundle.getMessage("angal.examination.bowel.diarrheal.txt"));
	}

	/**
	 * Return a list of diuresis descriptions:
	 * physiological,
	 * oliguria,
	 * anuria,
	 * fequent,
	 * nocturia,
	 * stranguria,
	 * hematuria,
	 * pyuria
	 *
	 * @return
	 */
	public List<String> getDiuresisDescriptionList() {
		if (diuresisDescriptionHashMap == null) {
			buildDiuresisDescriptionHashMap();
		}
		List<String> diuresisDescriptionList = new ArrayList<>(diuresisDescriptionHashMap.values());
		diuresisDescriptionList.sort(new DefaultSorter(MessageBundle.getMessage("angal.examination.diuresis.physiological.txt")));
		return diuresisDescriptionList;
	}

	/**
	 * Return a list of bowel descriptions:
	 * regular,
	 * irregular,
	 * constipation,
	 * diarrheal
	 *
	 * @return
	 */
	public List<String> getBowelDescriptionList() {
		if (bowelDescriptionHashMap == null) {
			buildBowelDescriptionHashMap();
		}
		List<String> bowelDescriptionList = new ArrayList<>(bowelDescriptionHashMap.values());
		bowelDescriptionList.sort(new DefaultSorter(MessageBundle.getMessage("angal.examination.bowel.regular.txt")));
		return bowelDescriptionList;
	}

	public String getBowelDescriptionTranslated(String pexBowelDescKey) {
		if (bowelDescriptionHashMap == null) {
			buildBowelDescriptionHashMap();
		}
		return bowelDescriptionHashMap.get(pexBowelDescKey);
	}

	public String getBowelDescriptionKey(String description) {
		if (bowelDescriptionHashMap == null) {
			buildBowelDescriptionHashMap();
		}
		for (Map.Entry<String, String> entry : bowelDescriptionHashMap.entrySet()) {
			if (entry.getValue().equals(description)) {
				return entry.getKey();
			}
		}
		return "";
	}

	public String getDiuresisDescriptionTranslated(String pexDiuresisDescKey) {
		if (diuresisDescriptionHashMap == null) {
			buildDiuresisDescriptionHashMap();
		}
		return diuresisDescriptionHashMap.get(pexDiuresisDescKey);
	}

	public String getDiuresisDescriptionKey(String description) {
		if (diuresisDescriptionHashMap == null) {
			buildDiuresisDescriptionHashMap();
		}
		for (Map.Entry<String, String> entry : diuresisDescriptionHashMap.entrySet()) {
			if (entry.getValue().equals(description)) {
				return entry.getKey();
			}
		}
		return "";
	}
}
