-- 참조 무결성 체크 비활성화
SET REFERENTIAL_INTEGRITY FALSE;

-- locations, departments, employees, job_history 테이블에 데이터 삽입
INSERT INTO locations SELECT * FROM CSVREAD('src/main/resources/hr/locations.csv', null, 'fieldSeparator=,');
INSERT INTO departments SELECT * FROM CSVREAD('src/main/resources/hr/departments.csv', null, 'fieldSeparator=,');
INSERT INTO employees SELECT * FROM CSVREAD('src/main/resources/hr/employees.csv', null, 'fieldSeparator=,');
INSERT INTO job_history SELECT * FROM CSVREAD('src/main/resources/hr/job_history.csv', null, 'fieldSeparator=,');

-- 참조 무결성 체크 재활성화
SET REFERENTIAL_INTEGRITY TRUE;
