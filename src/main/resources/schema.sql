DROP TABLE IF EXISTS job_history CASCADE;
DROP TABLE IF EXISTS employees CASCADE;
DROP TABLE IF EXISTS departments CASCADE;
DROP TABLE IF EXISTS locations CASCADE;

CREATE TABLE locations (
                           location_id INTEGER,
                           street_address VARCHAR(40),
                           postal_code VARCHAR(12),
                           city VARCHAR(30) NOT NULL,
                           state_province VARCHAR(25),
                           country_id CHAR(2),
                           PRIMARY KEY (location_id)
);

CREATE TABLE departments (
                             department_id INTEGER,
                             department_name VARCHAR(30) NOT NULL,
                             manager_id INTEGER,
                             location_id INTEGER,
                             PRIMARY KEY (department_id),
                             CONSTRAINT dept_loc_fk FOREIGN KEY (location_id)
                                 REFERENCES locations (location_id)
);

CREATE TABLE employees (
                           employee_id INTEGER,
                           first_name VARCHAR(20),
                           last_name VARCHAR(25) NOT NULL,
                           email VARCHAR(25) NOT NULL UNIQUE,
                           phone_number VARCHAR(20),
                           hire_date DATE NOT NULL,
                           job_id VARCHAR(10) NOT NULL,
                           salary DECIMAL(8,2) CHECK (salary > 0),
                           commission_pct DECIMAL(4,2),
                           manager_id INTEGER,
                           department_id INTEGER,
                           PRIMARY KEY (employee_id),
                           CONSTRAINT emp_dept_fk FOREIGN KEY (department_id)
                               REFERENCES departments (department_id),
                           CONSTRAINT emp_manager_fk FOREIGN KEY (manager_id)
                               REFERENCES employees (employee_id)
);

-- departments 테이블의 manager_id 외래키 추가
ALTER TABLE departments
    ADD CONSTRAINT dept_mgr_fk
        FOREIGN KEY (manager_id) REFERENCES employees (employee_id);

CREATE TABLE job_history (
                             employee_id INTEGER NOT NULL,
                             start_date DATE NOT NULL,
                             end_date DATE NOT NULL,
                             job_id VARCHAR(10) NOT NULL,
                             department_id INTEGER,
                             PRIMARY KEY (employee_id, start_date),
                             CONSTRAINT jhist_date_interval CHECK (end_date > start_date),
                             CONSTRAINT jhist_emp_fk FOREIGN KEY (employee_id)
                                 REFERENCES employees (employee_id),
                             CONSTRAINT jhist_dept_fk FOREIGN KEY (department_id)
                                 REFERENCES departments (department_id)
);
