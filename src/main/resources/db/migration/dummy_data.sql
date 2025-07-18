INSERT INTO jobs (title, description, location, salary_min, salary_max, job_type, employer_id) VALUES
                                                                                                   ('Backend Developer', 'Work on APIs and data pipelines.', 'New York', 80000, 120000, 'FULL_TIME', 2),
                                                                                                   ('Frontend Developer Intern', 'Build UI components and improve UX.', 'Remote', 0, 20000, 'INTERN', 2);

INSERT INTO applications (applicant_id, job_id, cover_letter, resume_link, status) VALUES
                                                                                       (1, 1, 'I am interested in the Backend role.', 'https://drive.com/resume_alice', 'PENDING'),
                                                                                       (1, 2, 'Excited to join as an intern.', 'https://drive.com/resume_bob', 'ACCEPTED');

INSERT INTO job_views (job_id, user_id) VALUES
                                            (1, 1), -- Alice viewed Backend job
                                            (2, 1); -- Alice also viewed Intern job
