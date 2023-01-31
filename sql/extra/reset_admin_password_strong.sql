-- reset admin password to 'Admin2022test!' without quotes.
UPDATE OH_USER SET US_PASSWD = '$2a$10$52y.1Y7ig9B6SQJy4hpPn.RscBZs7rh7fljh3GtC5RC8txi1O29NS' WHERE (US_ID_A = 'admin');
