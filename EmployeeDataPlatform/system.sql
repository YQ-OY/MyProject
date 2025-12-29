-- 1. 创建一个表空间tbs_stu
create tablespace tbs_stu
       datafile'd:\oracle\oradata\stu\tbs_stu.dbf'
       size 100M
       autoextend on
       next 50M
       maxsize unlimited;
       
-- 2. 定义一个用户tuster
create user tuster identified by visit
       default tablespace tbs_stu
       temporary tablespace temp
       quota 20M on tbs_stu
       account lock;       
       
-- 修改用户tuster状态为unlock
alter user tuster account unlock;

-- 授予该用户CREATE SESSION和SELECT ANY TABLE和CREATE USER权限
grant create session,select any table,create user to tuster;

-- 将CONNECT、RESOURCE 角色权限授予tuster
grant connect,resource to tuster;

-- 1. 授予EMP表的全部权限
GRANT ALL PRIVILEGES ON tuster.EMP TO tuster;

-- 2. 授予DEPT表的全部权限
GRANT ALL PRIVILEGES ON tuster.DEPT TO tuster;

-- 3. 授予SALARY表的全部权限
GRANT ALL PRIVILEGES ON tuster.SALARY TO tuster;

-- 4. 授予USERINFO表的全部权限
GRANT ALL PRIVILEGES ON tuster.USERINFO TO tuster;

-- 查看当前用户所有表的名称（按表名排序）
SELECT TABLE_NAME 
FROM USER_TABLES 
ORDER BY TABLE_NAME;

---------------------------------------------------------------------------------------------------------------------
-- emp 
CREATE TABLE emp (
    emp_id      NUMBER PRIMARY KEY,  -- 员工ID，主键
    manager_id  NUMBER,              -- 上级经理ID
    photo       VARCHAR2(255),       -- 员工照片路径/标识
    name        VARCHAR2(255),       -- 员工姓名
    gender      CHAR(3),             -- 性别
    dept_id     NUMBER,              -- 所属部门ID
    job         VARCHAR2(255),       -- 职位
    birthday    DATE,                -- 生日
    entryday    DATE,                -- 入职日期
    age         NUMBER(3) DEFAULT 0
);

-- dept
CREATE TABLE dept (
    dept_id        NUMBER PRIMARY KEY,  -- 部门ID，主键
    name           VARCHAR2(255),       -- 部门名称
    manager_emp_id NUMBER,              -- 部门主管的员工编号
    description    VARCHAR2(255)        -- 部门描述
);

-- salary
CREATE TABLE salary (
    salary_id     NUMBER PRIMARY KEY,  -- 薪资记录ID，主键
    emp_id        NUMBER,              -- 员工ID（无外键）
    base          NUMBER(10,2),        -- 基本工资
    subsidy       NUMBER(10,2),        -- 补贴
    fine          NUMBER(10,2),        -- 罚款
    total         NUMBER(11,2) 
        GENERATED ALWAYS AS (NVL(base, 0) + NVL(subsidy, 0) - NVL(fine, 0)) 
        VIRTUAL                        -- 明确指定为虚拟列（Oracle 11g+）
);

-- user
CREATE TABLE userInfo (
    user_id     NUMBER PRIMARY KEY, 
    name        VARCHAR2(255),             
    password    VARCHAR2(255),
    photo       VARCHAR2(255),
    email       VARCHAR2(255),
    phone       VARCHAR2(11),        
    gender      char(3)     
);

--------------------------------------------------------------------------------------------------------------------

-- 触发器（插入数据时自动计算年龄）
CREATE OR REPLACE TRIGGER trg_emp_calc_age
BEFORE INSERT OR UPDATE OF birthday ON emp  -- 插入或更新birthday时触发
FOR EACH ROW  -- 行级触发器，每一行数据都触发
BEGIN
    -- 计算年龄：MONTHS_BETWEEN(当前日期, 生日)/12 → 转年，TRUNC取整，NVL处理生日为空
    :NEW.AGE := TRUNC(NVL(MONTHS_BETWEEN(SYSDATE, :NEW.birthday)/12, 0));
END;
--------------------------------------------------------------------------------------------------------------------

-- 部门数据
INSERT INTO DEPT (DEPT_ID, NAME, MANAGER_EMP_ID, DESCRIPTION) VALUES (1, '人事部', NULL, '主管及董事人选皆需人事部裁决。');
INSERT INTO DEPT (DEPT_ID, NAME, MANAGER_EMP_ID, DESCRIPTION) VALUES (2, '组织部', NULL, '活动的"策划大师"，从构思到落地，每一场精彩都离不开我们的精心编织。');
INSERT INTO DEPT (DEPT_ID, NAME, MANAGER_EMP_ID, DESCRIPTION) VALUES (3, '秘书部', NULL, '公司的"中枢神经"，负责注册评定、文书起草和场地申请。');
INSERT INTO DEPT (DEPT_ID, NAME, MANAGER_EMP_ID, DESCRIPTION) VALUES (4, '宣传部', NULL, '公司的"发声筒"和"美容师"，用镜头捕捉瞬间，用画笔装点集团。');
INSERT INTO DEPT (DEPT_ID, NAME, MANAGER_EMP_ID, DESCRIPTION) VALUES (5, '商务部', NULL, '负责统筹外来企业宣讲会与公司面试，为公司运营与活动开展洽谈外部赞助。');
INSERT INTO DEPT (DEPT_ID, NAME, MANAGER_EMP_ID, DESCRIPTION) VALUES (6, '运营部', NULL, '聚焦就业服务与公司运营两大核心。');
INSERT INTO DEPT (DEPT_ID, NAME, MANAGER_EMP_ID, DESCRIPTION) VALUES (7, '保安处', NULL, '一人一狗，恪尽职守。');
INSERT INTO DEPT (DEPT_ID, NAME, MANAGER_EMP_ID, DESCRIPTION) VALUES (8, '董事会', NULL, '他们站在更高的视角，以更周全的考量规划公司发展方向。');
COMMIT;

-- 员工数据
-- 董事
INSERT INTO EMP (EMP_ID, MANAGER_ID, PHOTO, NAME, GENDER, DEPT_ID, JOB, BIRTHDAY, ENTRYDAY) 
VALUES (1001, NULL, '/photo/1001.jpg', '李阳', '男', 8, '董事长', TO_DATE('1975-03-10', 'YYYY-MM-DD'), TO_DATE('2000-05-01', 'YYYY-MM-DD'));

INSERT INTO EMP (EMP_ID, MANAGER_ID, PHOTO, NAME, GENDER, DEPT_ID, JOB, BIRTHDAY, ENTRYDAY) 
VALUES (1002, NULL, '/photo/1002.jpg', '徐锦涛', '男', 8, '董事长', TO_DATE('1978-07-22', 'YYYY-MM-DD'), TO_DATE('2002-08-15', 'YYYY-MM-DD'));

INSERT INTO EMP (EMP_ID, MANAGER_ID, PHOTO, NAME, GENDER, DEPT_ID, JOB, BIRTHDAY, ENTRYDAY) 
VALUES (1003, NULL, '/photo/1003.jpg', '翟广帅', '男', 8, '董事长', TO_DATE('1980-01-18', 'YYYY-MM-DD'), TO_DATE('2005-03-20', 'YYYY-MM-DD'));

INSERT INTO EMP (EMP_ID, MANAGER_ID, PHOTO, NAME, GENDER, DEPT_ID, JOB, BIRTHDAY, ENTRYDAY) 
VALUES (1004, NULL, '/photo/1004.jpg', '田明明', '男', 8, '董事长', TO_DATE('1976-11-05', 'YYYY-MM-DD'), TO_DATE('1999-10-08', 'YYYY-MM-DD'));

INSERT INTO EMP (EMP_ID, MANAGER_ID, PHOTO, NAME, GENDER, DEPT_ID, JOB, BIRTHDAY, ENTRYDAY) 
VALUES (1005, NULL, '/photo/1005.jpg', '谢晓彤', '女', 8, '董事长', TO_DATE('1979-09-30', 'YYYY-MM-DD'), TO_DATE('2003-06-12', 'YYYY-MM-DD')); 

INSERT INTO EMP (EMP_ID, MANAGER_ID, PHOTO, NAME, GENDER, DEPT_ID, JOB, BIRTHDAY, ENTRYDAY) 
VALUES (1006, NULL, '/photo/1007.jpg', '佳佳佳佳陈陈', '女', 8, '董事长', TO_DATE('1981-06-28', 'YYYY-MM-DD'), TO_DATE('2006-09-01', 'YYYY-MM-DD')); 

INSERT INTO EMP (EMP_ID, MANAGER_ID, PHOTO, NAME, GENDER, DEPT_ID, JOB, BIRTHDAY, ENTRYDAY) 
VALUES (1007, NULL, '/photo/1014.jpg', '小黄', '男', 8, '保安大队长', TO_DATE('2005-08-18', 'YYYY-MM-DD'), TO_DATE('2023-09-30', 'YYYY-MM-DD'));

COMMIT;


-- 部长
INSERT INTO EMP (EMP_ID, MANAGER_ID, PHOTO, NAME, GENDER, DEPT_ID, JOB, BIRTHDAY, ENTRYDAY) 
VALUES (1008, 1006, '/photo/1008.jpg', '陈佳', '女', 1, '部长', TO_DATE('1985-08-12', 'YYYY-MM-DD'), TO_DATE('2010-04-05', 'YYYY-MM-DD')); -- 人事部

INSERT INTO EMP (EMP_ID, MANAGER_ID, PHOTO, NAME, GENDER, DEPT_ID, JOB, BIRTHDAY, ENTRYDAY) 
VALUES (1009, 1001, '/photo/1009.jpg', '崔含阳', '男', 2, '部长', TO_DATE('1983-10-25', 'YYYY-MM-DD'), TO_DATE('2008-11-18', 'YYYY-MM-DD')); -- 组织部

INSERT INTO EMP (EMP_ID, MANAGER_ID, PHOTO, NAME, GENDER, DEPT_ID, JOB, BIRTHDAY, ENTRYDAY) 
VALUES (1010, 1002, '/photo/1010.jpg', '王怡梦', '女', 3, '部长', TO_DATE('1987-02-03', 'YYYY-MM-DD'), TO_DATE('2012-07-22', 'YYYY-MM-DD')); -- 秘书部

INSERT INTO EMP (EMP_ID, MANAGER_ID, PHOTO, NAME, GENDER, DEPT_ID, JOB, BIRTHDAY, ENTRYDAY) 
VALUES (1011, 1005, '/photo/1011.jpg', '赵侦宏', '女', 4, '部长', TO_DATE('1984-05-17', 'YYYY-MM-DD'), TO_DATE('2009-03-10', 'YYYY-MM-DD')); -- 宣传部

INSERT INTO EMP (EMP_ID, MANAGER_ID, PHOTO, NAME, GENDER, DEPT_ID, JOB, BIRTHDAY, ENTRYDAY) 
VALUES (1012, 1003, '/photo/1012.jpg', '何鑫月', '女', 5, '部长', TO_DATE('1986-09-08', 'YYYY-MM-DD'), TO_DATE('2011-08-01', 'YYYY-MM-DD')); -- 商务部

INSERT INTO EMP (EMP_ID, MANAGER_ID, PHOTO, NAME, GENDER, DEPT_ID, JOB, BIRTHDAY, ENTRYDAY) 
VALUES (1013, 1004, '/photo/1013.jpg', '李金泽', '女', 6, '部长', TO_DATE('1982-12-30', 'YYYY-MM-DD'), TO_DATE('2007-06-15', 'YYYY-MM-DD')); -- 运营部

INSERT INTO EMP (EMP_ID, MANAGER_ID, PHOTO, NAME, GENDER, DEPT_ID, JOB, BIRTHDAY, ENTRYDAY) 
VALUES (1014, 1007, '/photo/1014.jpg', '小胖', '男', 8, '神犬', TO_DATE('2005-08-18', 'YYYY-MM-DD'), TO_DATE('2023-09-30', 'YYYY-MM-DD'));

COMMIT;

-- 更新部门表关联部长
UPDATE DEPT SET MANAGER_EMP_ID = 1008 WHERE DEPT_ID = 1;
UPDATE DEPT SET MANAGER_EMP_ID = 1009 WHERE DEPT_ID = 2;
UPDATE DEPT SET MANAGER_EMP_ID = 1010 WHERE DEPT_ID = 3;
UPDATE DEPT SET MANAGER_EMP_ID = 1011 WHERE DEPT_ID = 4;
UPDATE DEPT SET MANAGER_EMP_ID = 1012 WHERE DEPT_ID = 5;
UPDATE DEPT SET MANAGER_EMP_ID = 1013 WHERE DEPT_ID = 6;
UPDATE DEPT SET MANAGER_EMP_ID = 1014 WHERE DEPT_ID = 7;
UPDATE DEPT SET MANAGER_EMP_ID = 1001 WHERE DEPT_ID = 8;
COMMIT;

-- 人事部
INSERT INTO EMP (EMP_ID, MANAGER_ID, PHOTO, NAME, GENDER, DEPT_ID, JOB, BIRTHDAY, ENTRYDAY) 
VALUES (1015, 1008, '/photo/1015.jpg', '徐鹏飞', '男', 1, '部员', TO_DATE('1990-03-14', 'YYYY-MM-DD'), TO_DATE('2015-05-20', 'YYYY-MM-DD')); -- 三字名
INSERT INTO EMP (EMP_ID, MANAGER_ID, PHOTO, NAME, GENDER, DEPT_ID, JOB, BIRTHDAY, ENTRYDAY) 
VALUES (1016, 1008, '/photo/1016.jpg', '朱浩', '男', 1, '部员', TO_DATE('1992-07-08', 'YYYY-MM-DD'), TO_DATE('2017-09-12', 'YYYY-MM-DD')); -- 两字名
INSERT INTO EMP (EMP_ID, MANAGER_ID, PHOTO, NAME, GENDER, DEPT_ID, JOB, BIRTHDAY, ENTRYDAY) 
VALUES (1017, 1008, '/photo/1017.jpg', '韩磊', '男', 1, '部员', TO_DATE('1991-11-22', 'YYYY-MM-DD'), TO_DATE('2016-03-05', 'YYYY-MM-DD')); -- 两字名
COMMIT;

-- 组织部
INSERT INTO EMP (EMP_ID, MANAGER_ID, PHOTO, NAME, GENDER, DEPT_ID, JOB, BIRTHDAY, ENTRYDAY) 
VALUES (1021, 1009, '/photo/1021.jpg', '吕晓丽', '女', 2, '部员', TO_DATE('1988-08-21', 'YYYY-MM-DD'), TO_DATE('2013-09-05', 'YYYY-MM-DD')); -- 三字名
INSERT INTO EMP (EMP_ID, MANAGER_ID, PHOTO, NAME, GENDER, DEPT_ID, JOB, BIRTHDAY, ENTRYDAY) 
VALUES (1022, 1009, '/photo/1022.jpg', '施敏', '女', 2, '部员', TO_DATE('1990-10-15', 'YYYY-MM-DD'), TO_DATE('2015-11-20', 'YYYY-MM-DD')); -- 两字名
INSERT INTO EMP (EMP_ID, MANAGER_ID, PHOTO, NAME, GENDER, DEPT_ID, JOB, BIRTHDAY, ENTRYDAY) 
VALUES (1023, 1009, '/photo/1023.jpg', '张佳怡', '女', 2, '部员', TO_DATE('1991-06-30', 'YYYY-MM-DD'), TO_DATE('2016-04-12', 'YYYY-MM-DD')); -- 三字名
INSERT INTO EMP (EMP_ID, MANAGER_ID, PHOTO, NAME, GENDER, DEPT_ID, JOB, BIRTHDAY, ENTRYDAY) 
VALUES (1027, 1009, '/photo/1027.jpg', '华军', '男', 2, '部员', TO_DATE('1993-07-24', 'YYYY-MM-DD'), TO_DATE('2018-08-30', 'YYYY-MM-DD')); -- 两字名（仅1男，无交替）
COMMIT;

-- 秘书部
INSERT INTO EMP (EMP_ID, MANAGER_ID, PHOTO, NAME, GENDER, DEPT_ID, JOB, BIRTHDAY, ENTRYDAY) 
VALUES (1028, 1010, '/photo/1028.jpg', '金伟豪', '男', 3, '部员', TO_DATE('1990-02-14', 'YYYY-MM-DD'), TO_DATE('2015-03-18', 'YYYY-MM-DD')); -- 三字名
INSERT INTO EMP (EMP_ID, MANAGER_ID, PHOTO, NAME, GENDER, DEPT_ID, JOB, BIRTHDAY, ENTRYDAY) 
VALUES (1029, 1010, '/photo/1029.jpg', '魏浩', '男', 3, '部员', TO_DATE('1988-07-09', 'YYYY-MM-DD'), TO_DATE('2013-10-22', 'YYYY-MM-DD')); -- 两字名
INSERT INTO EMP (EMP_ID, MANAGER_ID, PHOTO, NAME, GENDER, DEPT_ID, JOB, BIRTHDAY, ENTRYDAY) 
VALUES (1035, 1010, '/photo/1035.jpg', '喻婷', '女', 3, '部员', TO_DATE('1994-05-21', 'YYYY-MM-DD'), TO_DATE('2019-09-18', 'YYYY-MM-DD')); -- 两字名（仅1女，无交替）
COMMIT;

-- 宣传部
INSERT INTO EMP (EMP_ID, MANAGER_ID, PHOTO, NAME, GENDER, DEPT_ID, JOB, BIRTHDAY, ENTRYDAY) 
VALUES (1036, 1011, '/photo/1036.jpg', '佟嘉辉', '男', 4, '部员', TO_DATE('1990-06-18', 'YYYY-MM-DD'), TO_DATE('2015-08-10', 'YYYY-MM-DD')); -- 三字名
INSERT INTO EMP (EMP_ID, MANAGER_ID, PHOTO, NAME, GENDER, DEPT_ID, JOB, BIRTHDAY, ENTRYDAY) 
VALUES (1037, 1011, '/photo/1037.jpg', '黄嵩博', '男', 4, '部员', TO_DATE('1988-12-03', 'YYYY-MM-DD'), TO_DATE('2013-07-25', 'YYYY-MM-DD')); -- 两字名
INSERT INTO EMP (EMP_ID, MANAGER_ID, PHOTO, NAME, GENDER, DEPT_ID, JOB, BIRTHDAY, ENTRYDAY) 
VALUES (1038, 1011, '/photo/1038.jpg', '王可欣', '女', 4, '部员', TO_DATE('1991-09-22', 'YYYY-MM-DD'), TO_DATE('2016-04-17', 'YYYY-MM-DD')); -- 三字名
INSERT INTO EMP (EMP_ID, MANAGER_ID, PHOTO, NAME, GENDER, DEPT_ID, JOB, BIRTHDAY, ENTRYDAY) 
VALUES (1039, 1011, '/photo/1039.jpg', '乔思梦', '女', 4, '部员', TO_DATE('1989-03-15', 'YYYY-MM-DD'), TO_DATE('2014-10-09', 'YYYY-MM-DD')); -- 两字名
INSERT INTO EMP (EMP_ID, MANAGER_ID, PHOTO, NAME, GENDER, DEPT_ID, JOB, BIRTHDAY, ENTRYDAY) 
VALUES (1040, 1011, '/photo/1040.jpg', '周渤程', '男', 4, '部员', TO_DATE('1992-07-08', 'YYYY-MM-DD'), TO_DATE('2017-01-30', 'YYYY-MM-DD')); -- 两字名（仅1男，无交替）
COMMIT;

-- 商务部
INSERT INTO EMP (EMP_ID, MANAGER_ID, PHOTO, NAME, GENDER, DEPT_ID, JOB, BIRTHDAY, ENTRYDAY) 
VALUES (1041, 1012, '/photo/1041.jpg', '袁建军', '男', 5, '部员', TO_DATE('1989-08-14', 'YYYY-MM-DD'), TO_DATE('2014-05-22', 'YYYY-MM-DD')); -- 三字名
INSERT INTO EMP (EMP_ID, MANAGER_ID, PHOTO, NAME, GENDER, DEPT_ID, JOB, BIRTHDAY, ENTRYDAY) 
VALUES (1042, 1012, '/photo/1042.jpg', '柳浩', '男', 5, '部员', TO_DATE('1991-02-27', 'YYYY-MM-DD'), TO_DATE('2016-09-10', 'YYYY-MM-DD')); -- 两字名
INSERT INTO EMP (EMP_ID, MANAGER_ID, PHOTO, NAME, GENDER, DEPT_ID, JOB, BIRTHDAY, ENTRYDAY) 
VALUES (1049, 1012, '/photo/1049.jpg', '岑婷', '女', 5, '部员', TO_DATE('1991-06-22', 'YYYY-MM-DD'), TO_DATE('2016-03-18', 'YYYY-MM-DD')); -- 两字名（仅1女，无交替）
COMMIT;

-- 运营部
INSERT INTO EMP (EMP_ID, MANAGER_ID, PHOTO, NAME, GENDER, DEPT_ID, JOB, BIRTHDAY, ENTRYDAY) 
VALUES (1050, 1013, '/photo/1050.jpg', '薛晓梅', '女', 6, '部员', TO_DATE('1990-07-21', 'YYYY-MM-DD'), TO_DATE('2015-09-15', 'YYYY-MM-DD')); -- 三字名
INSERT INTO EMP (EMP_ID, MANAGER_ID, PHOTO, NAME, GENDER, DEPT_ID, JOB, BIRTHDAY, ENTRYDAY) 
VALUES (1051, 1013, '/photo/1051.jpg', '雷琳', '女', 6, '部员', TO_DATE('1988-03-10', 'YYYY-MM-DD'), TO_DATE('2013-12-20', 'YYYY-MM-DD')); -- 两字名
INSERT INTO EMP (EMP_ID, MANAGER_ID, PHOTO, NAME, GENDER, DEPT_ID, JOB, BIRTHDAY, ENTRYDAY) 
VALUES (1052, 1013, '/photo/1052.jpg', '贺佳怡', '女', 6, '部员', TO_DATE('1991-10-05', 'YYYY-MM-DD'), TO_DATE('2016-06-28', 'YYYY-MM-DD')); -- 三字名
INSERT INTO EMP (EMP_ID, MANAGER_ID, PHOTO, NAME, GENDER, DEPT_ID, JOB, BIRTHDAY, ENTRYDAY) 
VALUES (1059, 1013, '/photo/1059.jpg', '郝强', '男', 6, '部员', TO_DATE('1988-11-25', 'YYYY-MM-DD'), TO_DATE('2013-05-30', 'YYYY-MM-DD')); -- 两字名（仅2男，无交替）
COMMIT;

-- 保安处
INSERT INTO EMP (EMP_ID, MANAGER_ID, PHOTO, NAME, GENDER, DEPT_ID, JOB, BIRTHDAY, ENTRYDAY) 
VALUES (1060, 1007, '/photo/1060.jpg', '蘑菇头', '男', 7, '部员', TO_DATE('1995-01-20', 'YYYY-MM-DD'), TO_DATE('2023-10-01', 'YYYY-MM-DD')); 
INSERT INTO EMP (EMP_ID, MANAGER_ID, PHOTO, NAME, GENDER, DEPT_ID, JOB, BIRTHDAY, ENTRYDAY) 
VALUES (1061, 1007, '/photo/1061.jpg', '茅台', '男', 7, '部员', TO_DATE('1993-05-15', 'YYYY-MM-DD'), TO_DATE('2023-11-05', 'YYYY-MM-DD')); 
INSERT INTO EMP (EMP_ID, MANAGER_ID, PHOTO, NAME, GENDER, DEPT_ID, JOB, BIRTHDAY, ENTRYDAY) 
VALUES (1062, 1007, '/photo/1062.jpg', '闰土', '男', 7, '部员', TO_DATE('1996-08-12', 'YYYY-MM-DD'), TO_DATE('2023-12-01', 'YYYY-MM-DD')); 

COMMIT;


-- 薪资
-- 1. 董事会/特殊岗位（7人）
INSERT INTO salary (salary_id, emp_id, base, subsidy, fine) VALUES (1, 1001, 65000.00, 8000.00, 0.00);  -- 李阳（董事长）
INSERT INTO salary (salary_id, emp_id, base, subsidy, fine) VALUES (2, 1002, 62000.00, 7500.00, 0.00);  -- 徐锦涛（董事长）
INSERT INTO salary (salary_id, emp_id, base, subsidy, fine) VALUES (3, 1003, 68000.00, 8500.00, 0.00);  -- 翟广帅（董事长）
INSERT INTO salary (salary_id, emp_id, base, subsidy, fine) VALUES (4, 1004, 60000.00, 7000.00, 0.00);  -- 田明明（董事长）
INSERT INTO salary (salary_id, emp_id, base, subsidy, fine) VALUES (5, 1005, 66000.00, 7800.00, 0.00);  -- 谢晓彤（董事长）
INSERT INTO salary (salary_id, emp_id, base, subsidy, fine) VALUES (6, 1006, 63000.00, 7200.00, 50.00);  -- 佳佳佳佳陈陈（董事长，迟到罚款）
INSERT INTO salary (salary_id, emp_id, base, subsidy, fine) VALUES (7, 1007, 8000.00, 2000.00, 0.00);   -- 小黄（保安大队长）
INSERT INTO salary (salary_id, emp_id, base, subsidy, fine) VALUES (8, 1014, 5000.00, 1000.00, 0.00);   -- 小胖（神犬）

-- 2. 各部门部长（7人）
INSERT INTO salary (salary_id, emp_id, base, subsidy, fine) VALUES (9, 1008, 30000.00, 3500.00, 0.00);   -- 陈佳（人事部部长）
INSERT INTO salary (salary_id, emp_id, base, subsidy, fine) VALUES (10, 1009, 28000.00, 3200.00, 80.00);  -- 崔含阳（组织部部长，忘打卡罚款）
INSERT INTO salary (salary_id, emp_id, base, subsidy, fine) VALUES (11, 1010, 29000.00, 3300.00, 0.00);  -- 王怡梦（秘书部部长）
INSERT INTO salary (salary_id, emp_id, base, subsidy, fine) VALUES (12, 1011, 31000.00, 3600.00, 0.00);  -- 赵侦宏（宣传部部长）
INSERT INTO salary (salary_id, emp_id, base, subsidy, fine) VALUES (13, 1012, 32000.00, 3800.00, 0.00);  -- 何鑫月（商务部部长）
INSERT INTO salary (salary_id, emp_id, base, subsidy, fine) VALUES (14, 1013, 27000.00, 3000.00, 50.00);  -- 李金泽（运营部部长，卫生不合格罚款）

-- 3. 人事部部员（3人）
INSERT INTO salary (salary_id, emp_id, base, subsidy, fine) VALUES (15, 1015, 9000.00, 1200.00, 20.00);  -- 徐鹏飞（忘交报表罚款）
INSERT INTO salary (salary_id, emp_id, base, subsidy, fine) VALUES (16, 1016, 8500.00, 1000.00, 0.00);   -- 朱浩
INSERT INTO salary (salary_id, emp_id, base, subsidy, fine) VALUES (17, 1017, 8800.00, 1100.00, 0.00);   -- 韩磊

-- 4. 组织部部员（4人）
INSERT INTO salary (salary_id, emp_id, base, subsidy, fine) VALUES (18, 1021, 8200.00, 900.00, 0.00);    -- 吕晓丽
INSERT INTO salary (salary_id, emp_id, base, subsidy, fine) VALUES (19, 1022, 8000.00, 800.00, 0.00);    -- 施敏
INSERT INTO salary (salary_id, emp_id, base, subsidy, fine) VALUES (20, 1023, 8400.00, 950.00, 0.00);    -- 张佳怡
INSERT INTO salary (salary_id, emp_id, base, subsidy, fine) VALUES (21, 1027, 8600.00, 1000.00, 30.00);  -- 华军（迟到罚款）

-- 5. 秘书部部员（3人）
INSERT INTO salary (salary_id, emp_id, base, subsidy, fine) VALUES (22, 1028, 9200.00, 1300.00, 0.00);  -- 金伟豪
INSERT INTO salary (salary_id, emp_id, base, subsidy, fine) VALUES (23, 1029, 8900.00, 1150.00, 0.00);  -- 魏浩
INSERT INTO salary (salary_id, emp_id, base, subsidy, fine) VALUES (24, 1035, 8300.00, 900.00, 0.00);   -- 喻婷

-- 6. 宣传部部员（5人）
INSERT INTO salary (salary_id, emp_id, base, subsidy, fine) VALUES (25, 1036, 9500.00, 1400.00, 0.00);  -- 佟嘉辉
INSERT INTO salary (salary_id, emp_id, base, subsidy, fine) VALUES (26, 1037, 9100.00, 1250.00, 0.00);  -- 黄嵩博
INSERT INTO salary (salary_id, emp_id, base, subsidy, fine) VALUES (27, 1038, 8700.00, 1050.00, 0.00);  -- 王可欣
INSERT INTO salary (salary_id, emp_id, base, subsidy, fine) VALUES (28, 1039, 8100.00, 850.00, 0.00);   -- 乔思梦
INSERT INTO salary (salary_id, emp_id, base, subsidy, fine) VALUES (29, 1040, 9300.00, 1350.00, 10.00);  -- 周渤程（卫生罚款）

-- 7. 商务部部员（3人）
INSERT INTO salary (salary_id, emp_id, base, subsidy, fine) VALUES (30, 1041, 9400.00, 1400.00, 0.00);  -- 袁建军
INSERT INTO salary (salary_id, emp_id, base, subsidy, fine) VALUES (31, 1042, 8900.00, 1200.00, 0.00);  -- 柳浩
INSERT INTO salary (salary_id, emp_id, base, subsidy, fine) VALUES (32, 1049, 8200.00, 900.00, 0.00);   -- 岑婷

-- 8. 运营部部员（4人）
INSERT INTO salary (salary_id, emp_id, base, subsidy, fine) VALUES (33, 1050, 8800.00, 1100.00, 0.00);  -- 薛晓梅
INSERT INTO salary (salary_id, emp_id, base, subsidy, fine) VALUES (34, 1051, 8000.00, 800.00, 0.00);   -- 雷琳
INSERT INTO salary (salary_id, emp_id, base, subsidy, fine) VALUES (35, 1052, 8500.00, 950.00, 0.00);   -- 贺佳怡
INSERT INTO salary (salary_id, emp_id, base, subsidy, fine) VALUES (36, 1059, 9000.00, 1200.00, 0.00);  -- 郝强

-- 9. 保安处部员（3人）
INSERT INTO salary (salary_id, emp_id, base, subsidy, fine) VALUES (37, 1060, 6000.00, 1500.00, 20.00);  -- 张强
INSERT INTO salary (salary_id, emp_id, base, subsidy, fine) VALUES (38, 1061, 5800.00, 1400.00, 0.00);   -- 刘勇
INSERT INTO salary (salary_id, emp_id, base, subsidy, fine) VALUES (39, 1062, 5900.00, 1450.00, 0.00);   -- 王丽

-- 提交所有薪资数据，永久生效
COMMIT;