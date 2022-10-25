-- insert initial test data
-- the IDs are hardcoded to enable references between further test data
-- negative IDs are used to not interfere with user-entered data and allow clean deletion of test data

DELETE FROM horse where id < 0;
DELETE FROM owner where id < 0;

INSERT INTO owner (id, first_name, last_name, email)
VALUES
    (-11, 'Adam', 'Skuta', 'adam.skuta@ttt.com'),
    (-12, 'Jozef', 'Schmidt', 'j.s@e.com'),
    (-13, 'Dedo', 'Stary', 'd.s@e.com'),
    (-14, 'Andrej', 'Slayer', 'a.s@e.com'),
    (-15, 'Ada', 'Slayer', 'a.s@e.com'),
    (-16, 'Samo', 'Wido', 's.w@e.com'),
    (-17, 'Eva', 'Silna', 'e.s@e.com'),
    (-18, 'Michal', 'Slany', 'm.s@e.com'),
    (-19, 'Katka', 'Slana', 'k.s@e.com'),
    (-20, 'Karol', 'Grill', 'k.g@e.com')
;

INSERT INTO horse (id, name, description, date_of_birth, sex, owner_id, father_id, mother_id)
VALUES
    (-1, 'Wendy', 'The famous one!', '2012-12-12', 'FEMALE', -11, null, null),
    (-2, 'Bucky', 'The cool one!', '2012-10-08', 'MALE', -12, null, null),
    (-3, 'Tricky', 'The insane horse', '2022-10-10', 'MALE', -12, -2, -1),
    (-4, 'Dambo', 'The big horse', '2022-10-20', 'MALE', -13, -3, null),
    (-5, 'Buck', 'The youngest horse', '2022-10-22', 'FEMALE', -14, -2, -1),
    (-6, 'Krill', 'The smart horse', '2009-10-12', 'FEMALE', -15, null, null),
    (-7, 'Bill', 'The smartest horse', '2010-04-04', 'MALE', -16, null, null),
    (-8, 'Radagon', 'The red horse', '2013-03-03', 'MALE', -17, -7, -6),
    (-9, 'Ranni', 'Blessed with the moon', '2015-09-09', 'FEMALE', -18, -8, -6),
    (-10, 'Blaidd', 'The ferocious one', '2017-10-10', 'MALE', -19, -8, -9)
;
