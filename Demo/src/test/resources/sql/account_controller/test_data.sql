INSERT INTO application_user (login, password, balance)
    VALUES ('login1', '$2a$10$k0OmFCyYmCE3fPa9klkIm.AkWE0r6mzR9IKHRPG90IjAPmZmjuwfu', 100000);

INSERT INTO payment (date, phone_number, amount, user_id)
    VALUES ('2024-03-17 15:43:35.026', '+79876543210', 15000, 'login1');


