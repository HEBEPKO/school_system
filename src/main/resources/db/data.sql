--Вставка в таблицу teachers


INSERT INTO teachers(
	first_name, second_name, last_name, subject, email, phone, is_class_teacher)
	VALUES ('Татьяна',
	'Ивановна',
	'Миронович',
	'труды',
	'mironovisch@mail.ru',
	'+375291234567',
	true),
	('Галина',
    'Ивановна',
    'Тишакова',
    'труды',
    'tichacova@mail.ru',
    '+375291234567',
    true);

--Вставка в таблицу students

INSERT INTO students(
	first_name, second_name, last_name, birth_date, email, phone)
	VALUES (
          'Алекцсей',
          'Невкрко',
          'Анатольевич',
          '24-11-1986',
          'aaneverko@mail.ru',
          '+375295043909'
          ),(
          'Ольга',
          'Максимович',
          'Константиновна',
          '07-09-1986',
          'kolok.olb4ik@mail.ru',
          '+375259325038'
          ) ,(
          'Ирина',
          'Петунова',
          'Анатольевна',
          '16-03-1987',
          '2792991@mail.ru',
          '+375292792991йцц33'
          );

--Вставка в таблицу classes

INSERT INTO classes(
	class_name, class_teachers_id, academic_year)
	VALUES ('8B', 1, 2025), ('8A', 2, 2025);