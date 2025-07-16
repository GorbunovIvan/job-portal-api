-- ======================
-- USERS
-- ======================
INSERT INTO users (email, password, full_name) VALUES
    ('applicant@example.com', '$2y$10$UK9E5V.IHNRXfeZkGkHfCeg.vNVONJbtiCa3x3RaD.klY91D1YE7y', 'Alice Junior'),
    ('employer@example.com', '$2y$10$R1hFnEdqgxQVQoqPlEOE3uBHne/IzZ1HE/Wnm/bHAMhMh02zGiylK', 'Carol Recruiter');

-- ======================
-- ROLES
-- ======================
INSERT INTO roles (name) VALUES
    ('ADMIN'),
    ('APPLICANT'),
    ('EMPLOYER');

INSERT INTO user_roles (user_id, role_id) VALUES
    (1, 2), -- Alice: APPLICANT
    (2, 3); -- Carol: EMPLOYER

-- ======================
-- APPLICANTS
-- ======================
INSERT INTO applicants (id, resume_link, bio) VALUES
    (1, 'https://drive.com/resume_alice', 'Software engineer with 5 years experience');

-- ======================
-- EMPLOYERS
-- ======================
INSERT INTO employers (id, company_name, company_website) VALUES
    (2, 'Tech Corp', 'https://techcorp.com');
