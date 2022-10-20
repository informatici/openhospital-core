UNLOCK TABLES;

LOCK TABLES `diseasetype` WRITE;
/*!40000 ALTER TABLE `diseasetype` DISABLE KEYS */;
INSERT INTO `diseasetype` VALUES ('AO','5. All Other',NULL,NULL,NULL,NULL,1);
INSERT INTO `diseasetype` VALUES ('MP','3.MATERNAL AND PERINATAL DISEASES',NULL,NULL,NULL,NULL,1);
INSERT INTO `diseasetype` VALUES ('NC','4.NON-COMMUNICABLE DISEASES',NULL,NULL,NULL,NULL,1);
INSERT INTO `diseasetype` VALUES ('ND','1.NOTIFIABLE DISEASES',NULL,NULL,NULL,NULL,1);
INSERT INTO `diseasetype` VALUES ('OC','2.OTHER INFECTIOUS/COMMUNICABLE DISEASES',NULL,NULL,NULL,NULL,1);

UNLOCK TABLES;
LOCK TABLES `disease` WRITE;
/*!40000 ALTER TABLE `disease` DISABLE KEYS */;
INSERT INTO `disease` VALUES ('1','Acute Flaccid Paralysis','ND',0,1,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('10','Viral Haemorragic Fever','ND',0,1,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('100','Other malignant neoplasm','NC',0,1,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('101','Curable Ulcers','NC',0,1,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('102','Cerebro-vascular event','NC',0,1,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('103','Cardiac arrest','NC',0,1,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('104','Gastro-intestinal bleeding','NC',0,1,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('105','Respiratory distress','NC',0,1,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('106','Acute renal failure','NC',1,0,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('107','Acute sepsis','NC',1,0,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('108','Schostosomiasis','NC',0,1,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('11','Yellow Fever','ND',0,1,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('110','Pelvic Inflammatory DISEASEs (PID)','OC',0,1,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('111','Tetanus (over 28 days age)','OC',1,1,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('112','Obstructed Labour','MP',0,1,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('113','Other types of meningitis','OC',0,1,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('114','Schistosomiasis','OC',0,1,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('115','Sleeping sickness','OC',0,1,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('116','Malaria in pregnancy','MP',0,1,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('117','Injuries - (road traffic accident)','NC',0,1,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('12','Anaemia','NC',0,1,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('127','Covid-19','ND',0,1,1,0,'admin','2020-11-22 02:56:26','admin','2020-11-22 02:56:26',1);
INSERT INTO `disease` VALUES ('13','Dental DISEASE and conditions','NC',0,1,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('14','Diabetes Mellitus','NC',0,1,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('15','Gastro-intestinal DISEASEss (non infective)','NC',1,1,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('16','Hypertension','NC',0,1,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('17','Mental Illness','NC',0,1,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('18','Epilepsy','NC',0,1,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('19','Other cardio-vascular DISEASEs','NC',0,1,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('2','Cholera','ND',0,1,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('20','Severe malnutrition (marasmus,kwashiorkor,marasmic-kwash)','NC',6,1,0,0,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('22','Trauma-Domestic Violence','NC',0,1,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('23','Trauma-other intensional','NC',0,1,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('24','Trauma Road traffic accidents','NC',0,1,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('25','Trauma Other Non intensional','NC',0,1,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('26','Other complications of pregnancy','MP',1,0,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('27','Perinatal conditions','MP',0,1,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('28','Abortions','MP',0,1,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('29','AIDS','OC',0,1,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('3','Diarrhoea-Dysentry','ND',2,1,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('30','Diarrhoea-Not bloody','OC',0,1,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('31','Diarrhoea-Persistent','OC',0,1,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('32','Ear Infection','OC',1,0,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('33','Eye Infection','OC',0,1,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('34','Genital Inf.-Urethral discharge','OC',1,0,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('35','Genital Inf.-Vaginal discharge','OC',1,0,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('36','Genital Inf.-Ulcerative','OC',1,0,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('37','Intestinal Worms','OC',0,1,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('38','Leprosy','OC',0,1,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('39','Malaria','OC',0,1,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('4','Guinea Worm','ND',0,1,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('40','No pneumonia-cold or cough','OC',0,1,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('41','Onchocerciasis','OC',0,1,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('42','Pneumonia','OC',0,1,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('43','Skin DISEASEs','OC',0,1,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('44','Tuberculosis','OC',2,1,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('46','Typhoid Fever','OC',0,1,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('47','Urinary Tract Infections (UTI)','OC',2,1,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('48','Others(non specified)','OC',1,0,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('49','All Other DISEASEs','AO',2,1,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('5','Measles','ND',0,1,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('50','Other emerging infectious DISEASE','ND',0,1,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('56','Death in OPD','OC',0,1,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('57','ENT Conditions','OC',0,1,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('58','Eye Conditions','OC',0,1,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('59','Sexually transmited infections (STI)','OC',1,1,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('6','Meningitis','ND',0,1,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('60','Hepatitis','OC',1,0,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('61','Osteomyelitis','OC',1,0,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('62','Peritonitis','OC',1,0,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('63','Pyrexiaof unknown origin (PUO)','OC',1,0,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('64','Septicaemia','OC',1,0,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('65','High blood pressure in pregnancy','MP',0,1,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('66','Haemorrhage related to pregnancy (APH/PPH)','MP',0,1,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('67','Sepsis related to pregnancy','MP',1,0,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('68','Asthma','NC',0,1,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('69','Oral DISEASEs and condition','NC',0,1,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('7','Tetanus neonatal (less 28 days age)','ND',3,1,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('70','Endocrine and metabolic disorders (other)','NC',0,1,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('71','Anxiety disorders','NC',0,1,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('72','Mania','NC',0,1,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('73','Depression','NC',0,1,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('74','Schizophrenia','NC',0,1,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('75','Alcohol and drug abuse','NC',0,1,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('76','Childhood and mentle disorders','NC',0,1,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('77','Severe malnutrition (kwashiorkor)','NC',2,0,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('78','Severe malnutrition (marasmus)','NC',3,0,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('79','Severe malnutrition (marasmic-kwash)','NC',2,0,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('8','Plague','ND',0,1,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('80','Low weight for age','NC',0,1,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('81','Injuries - (trauma due to other causes)','NC',0,1,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('82','Animal/snake bite','NC',0,1,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('83','Poisoning','NC',1,0,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('84','Liver cirrhosis','NC',0,1,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('85','Hepatocellular carcinoma','NC',0,1,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('86','Liver DISEASE s (other)','NC',0,1,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('87','Hernias','NC',0,1,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('88','DISEASEs of the appendix','NC',0,1,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('89','Musculo skeletal and connective tissue DISEASE','NC',0,1,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('9','Rabies','ND',0,1,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('90','Genitourinary system DISEASEs ( non infective )','NC',0,1,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('91','Congenital malformations and chromosome abnormalities','ND',1,0,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('92','Complication and surgical care','NC',0,1,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('93','Benine neoplasm\"s ( all type )','NC',0,1,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('94','Cancer of the breast','NC',0,1,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('95','Cancer of the prostate','ND',1,0,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('96','Malignant neoplasm of the digestive organs','NC',0,1,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('97','Malignant of the lungs','NC',0,1,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('98','Caposis and other skin cancers','NC',0,1,1,1,NULL,NULL,NULL,NULL,1);
INSERT INTO `disease` VALUES ('99','Malignant neoplasm of Haemopoetic tissue','NC',0,1,1,1,NULL,NULL,NULL,NULL,1);