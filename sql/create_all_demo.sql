#script creazione db
source step_01_create_structure.sql;
source step_02_dump_menu.sql;
source step_03_dump_default_data_en.sql;
source step_04_all_following_steps.sql;
source step_03_dump_vaccine_data_en.sql;
source delete_all_data.sql;
source load_demo_data.sql;
