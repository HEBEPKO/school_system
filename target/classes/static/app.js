class SchoolSystem {
    constructor() {
        this.apiUrl = 'http://localhost:8080/api';
        this.currentClass = null;
        this.currentPage = {
            students: 1,
            teachers: 1
        };
        this.itemsPerPage = 12;
        this.activeTab = 'classes';
        this.initEventListeners();
        this.loadFilterOptions();
    }

    initEventListeners() {
        document.getElementById('search-btn').addEventListener('click', () => this.searchClass());
        document.getElementById('class-search').addEventListener('keypress', (e) => {
            if (e.key === 'Enter') this.searchClass();
        });
        document.getElementById('create-class-btn').addEventListener('click', () => this.createClass());

        // Вкладки навигации
        document.getElementById('classes-tab').addEventListener('click', (e) => this.switchTab(e, 'classes'));
        document.getElementById('students-tab').addEventListener('click', (e) => this.switchTab(e, 'students'));
        document.getElementById('teachers-tab').addEventListener('click', (e) => this.switchTab(e, 'teachers'));

        // Поиск в списках
        document.getElementById('students-search').addEventListener('input', (e) => this.filterStudents(e.target.value));
        document.getElementById('students-class-filter').addEventListener('change', (e) => this.filterStudentsByClass(e.target.value));

        document.getElementById('teachers-search').addEventListener('input', (e) => this.filterTeachers(e.target.value));
        document.getElementById('teachers-subject-filter').addEventListener('change', (e) => this.filterTeachersBySubject(e.target.value));
    }

    switchTab(event, tabName) {
        event.preventDefault();

        // Убираем активный класс со всех вкладок
        document.querySelectorAll('.nav-links a').forEach(link => {
            link.classList.remove('active');
        });

        // Добавляем активный класс к текущей вкладке
        event.target.classList.add('active');

        // Скрываем все контенты вкладок
        document.querySelectorAll('.tab-content').forEach(content => {
            content.classList.remove('active');
        });

        // Показываем контент выбранной вкладки
        const tabContent = document.getElementById(`${tabName}-tab-content`);
        if (tabContent) {
            tabContent.classList.add('active');
            this.activeTab = tabName;

            // Загружаем данные для вкладок при первом открытии
            if (tabName === 'students' && !this.studentsLoaded) {
                this.loadStudents();
            } else if (tabName === 'teachers' && !this.teachersLoaded) {
                this.loadTeachers();
            }
        } else {
            this.showError(`Вкладка "${tabName}" не найдена`);
        }

        // Скрываем сообщения об ошибках при переключении
        this.hideError();
    }

    // Загрузка опций для фильтров
    async loadFilterOptions() {
        try {
            // Загрузка доступных классов для фильтра учеников
            const classesResponse = await fetch(`${this.apiUrl}/classes/search/classes`);
            const classesData = await classesResponse.json();

            if (classesData.success && classesData.data) {
                const classFilter = document.getElementById('students-class-filter');
                classFilter.innerHTML = '<option value="">Все классы</option>' +
                    classesData.data.map(className =>
                        `<option value="${className}">${className}</option>`
                    ).join('');
            }

            // Загрузка доступных предметов для фильтра учителей
            const subjectsResponse = await fetch(`${this.apiUrl}/teachers/subjects`);
            const subjectsData = await subjectsResponse.json();

            if (subjectsData.success && subjectsData.data) {
                const subjectFilter = document.getElementById('teachers-subject-filter');
                subjectFilter.innerHTML = '<option value="">Все предметы</option>' +
                    subjectsData.data.map(subject =>
                        `<option value="${subject}">${subject}</option>`
                    ).join('');
            }
        } catch (error) {
            console.error('Error loading filter options:', error);
        }
    }

    // Загрузка списка учеников
    async loadStudents(page = 1) {
        this.showLoading(true);
        this.hideError();
        this.currentPage.students = page;

        try {
            const searchQuery = document.getElementById('students-search')?.value || '';
            const classFilter = document.getElementById('students-class-filter')?.value || '';

            const params = new URLSearchParams({
                page: page,
                size: this.itemsPerPage,
                search: searchQuery,
                className: classFilter
            });

            const response = await fetch(`${this.apiUrl}/students?${params.toString()}`);
            const data = await response.json();

            if (data.success && data.data) {
                this.displayStudents(data.data.content, data.data.totalElements, data.data.totalPages);
                this.studentsLoaded = true;
            } else {
                this.showError(data.message || 'Не удалось загрузить список учеников');
                this.clearStudentsContainer();
            }
        } catch (error) {
            console.error('Error loading students:', error);
            this.showError('Ошибка при загрузке списка учеников. Проверьте соединение с сервером.');
            this.clearStudentsContainer();
        } finally {
            this.showLoading(false);
        }
    }

    displayStudents(students, totalStudents, totalPages) {
        const container = document.getElementById('students-container');

        if (!students || students.length === 0) {
            container.innerHTML = `
                <div class="no-results">
                    <h3>👥 Ученики не найдены</h3>
                    <p>Нет учеников, соответствующих критериям поиска</p>
                    <button class="secondary-btn" onclick="schoolSystem.resetStudentsFilter()">Сбросить фильтры</button>
                </div>
            `;
            this.renderPagination('students', 1, 1);
            return;
        }

        const studentsHTML = students.map(student => `
            <div class="student-card">
                <div class="student-name">
                    👤 ${student.fullName}
                    ${student.class ? `<span class="class-badge">${student.class}</span>` : ''}
                </div>
                <div class="student-info">📧 ${student.email || 'Не указано'}</div>
                <div class="student-info">📱 ${student.phone || 'Не указано'}</div>
                <div class="student-info">🎂 ${student.birthDate ? new Date(student.birthDate).toLocaleDateString('ru-RU') : 'Не указано'}</div>
                <div class="contact-info">
                    ${student.parentName ? `<div class="contact-item">👨‍👩‍👧‍👦 Родитель: ${student.parentName}</div>` : ''}
                    ${student.parentPhone ? `<div class="contact-item">📱 ${student.parentPhone}</div>` : ''}
                </div>
                <button class="details-btn" onclick="schoolSystem.showStudentDetails(${student.id})">
                    📋 Подробнее
                </button>
            </div>
        `).join('');

        container.innerHTML = `
            <div class="students-grid">
                ${studentsHTML}
            </div>
            <div class="total-count">
                Всего учеников: <strong>${totalStudents}</strong>
            </div>
        `;

        this.renderPagination('students', this.currentPage.students, totalPages);
    }

    // Загрузка списка учителей
    async loadTeachers(page = 1) {
        this.showLoading(true);
        this.hideError();
        this.currentPage.teachers = page;

        try {
            const searchQuery = document.getElementById('teachers-search')?.value || '';
            const subjectFilter = document.getElementById('teachers-subject-filter')?.value || '';

            const params = new URLSearchParams({
                page: page,
                size: this.itemsPerPage,
                search: searchQuery,
                subject: subjectFilter
            });

            const response = await fetch(`${this.apiUrl}/teachers?${params.toString()}`);
            const data = await response.json();

            if (data.success && data.data) {
                this.displayTeachers(data.data.content, data.data.totalElements, data.data.totalPages);
                this.teachersLoaded = true;
            } else {
                this.showError(data.message || 'Не удалось загрузить список учителей');
                this.clearTeachersContainer();
            }
        } catch (error) {
            console.error('Error loading teachers:', error);
            this.showError('Ошибка при загрузке списка учителей. Проверьте соединение с сервером.');
            this.clearTeachersContainer();
        } finally {
            this.showLoading(false);
        }
    }

    displayTeachers(teachers, totalTeachers, totalPages) {
        const container = document.getElementById('teachers-container');

        if (!teachers || teachers.length === 0) {
            container.innerHTML = `
                <div class="no-results">
                    <h3>👩‍🏫 Учителя не найдены</h3>
                    <p>Нет учителей, соответствующих критериям поиска</p>
                    <button class="secondary-btn" onclick="schoolSystem.resetTeachersFilter()">Сбросить фильтры</button>
                </div>
            `;
            this.renderPagination('teachers', 1, 1);
            return;
        }

        const teachersHTML = teachers.map(teacher => `
            <div class="teacher-card">
                <div class="teacher-name">
                    👨‍🏫 ${teacher.fullName}
                    ${teacher.classTeacher ? `<span class="class-teacher-badge">Классный руководитель</span>` : ''}
                </div>
                <div class="teacher-info">📚 Предмет: <span class="subject-badge">${teacher.subject}</span></div>
                <div class="teacher-info">📧 ${teacher.email}</div>
                <div class="teacher-info">📱 ${teacher.phone || 'Не указано'}</div>
                <div class="contact-info">
                    ${teacher.classes && teacher.classes.length > 0 ?
                        `<div class="contact-item">📋 Классы: ${teacher.classes.join(', ')}</div>` : ''}
                </div>
                <button class="details-btn" onclick="schoolSystem.showTeacherDetails(${teacher.id})">
                    📋 Подробнее
                </button>
            </div>
        `).join('');

        container.innerHTML = `
            <div class="teachers-grid">
                ${teachersHTML}
            </div>
            <div class="total-count">
                Всего учителей: <strong>${totalTeachers}</strong>
            </div>
        `;

        this.renderPagination('teachers', this.currentPage.teachers, totalPages);
    }

    // Фильтрация учеников
    filterStudents(searchQuery) {
        if (this.activeTab === 'students') {
            this.currentPage.students = 1;
            this.loadStudents(1);
        }
    }

    filterStudentsByClass(className) {
        if (this.activeTab === 'students') {
            this.currentPage.students = 1;
            this.loadStudents(1);
        }
    }

    // Фильтрация учителей
    filterTeachers(searchQuery) {
        if (this.activeTab === 'teachers') {
            this.currentPage.teachers = 1;
            this.loadTeachers(1);
        }
    }

    filterTeachersBySubject(subject) {
        if (this.activeTab === 'teachers') {
            this.currentPage.teachers = 1;
            this.loadTeachers(1);
        }
    }

    // Рендеринг пагинации
    renderPagination(type, currentPage, totalPages) {
        if (totalPages <= 1) return;

        const paginationContainer = document.createElement('div');
        paginationContainer.className = 'pagination';

        // Кнопка "Предыдущая"
        paginationContainer.innerHTML += `
            <button ${currentPage === 1 ? 'disabled' : ''} onclick="schoolSystem.${type === 'students' ? 'loadStudents' : 'loadTeachers'}(${currentPage - 1})">
                ←
            </button>
        `;

        // Кнопки страниц
        const maxButtons = 5;
        let startPage = Math.max(1, currentPage - Math.floor(maxButtons / 2));
        let endPage = Math.min(totalPages, startPage + maxButtons - 1);

        if (endPage - startPage + 1 < maxButtons) {
            startPage = Math.max(1, endPage - maxButtons + 1);
        }

        for (let i = startPage; i <= endPage; i++) {
            paginationContainer.innerHTML += `
                <button ${i === currentPage ? 'class="active"' : ''} onclick="schoolSystem.${type === 'students' ? 'loadStudents' : 'loadTeachers'}(${i})">
                    ${i}
                </button>
            `;
        }

        // Кнопка "Следующая"
        paginationContainer.innerHTML += `
            <button ${currentPage === totalPages ? 'disabled' : ''} onclick="schoolSystem.${type === 'students' ? 'loadStudents' : 'loadTeachers'}(${currentPage + 1})">
                →
            </button>
        `;

        // Добавляем пагинацию в соответствующий контейнер
        const container = type === 'students' ?
            document.getElementById('students-container') :
            document.getElementById('teachers-container');

        // Удаляем существующую пагинацию
        const existingPagination = container.querySelector('.pagination');
        if (existingPagination) {
            existingPagination.remove();
        }

        container.appendChild(paginationContainer);
    }

    // Очистка контейнеров
    clearStudentsContainer() {
        document.getElementById('students-container').innerHTML = `
            <div class="no-data">
                <p>Список учеников пуст</p>
            </div>
        `;
    }

    clearTeachersContainer() {
        document.getElementById('teachers-container').innerHTML = `
            <div class="no-data">
                <p>Список учителей пуст</p>
            </div>
        `;
    }

    // Сброс фильтров
    resetStudentsFilter() {
        document.getElementById('students-search').value = '';
        document.getElementById('students-class-filter').value = '';
        this.loadStudents(1);
    }

    resetTeachersFilter() {
        document.getElementById('teachers-search').value = '';
        document.getElementById('teachers-subject-filter').value = '';
        this.loadTeachers(1);
    }

    // Показ деталей
    showStudentDetails(studentId) {
        this.showError(`Детали ученика с ID ${studentId} будут реализованы в будущем`);
    }

    showTeacherDetails(teacherId) {
        this.showError(`Детали учителя с ID ${teacherId} будут реализованы в будущем`);
    }

    // Существующие методы (searchClass, createClass, etc.)
    async searchClass() {
        const className = document.getElementById('class-search').value.trim();
        if (!className) {
            this.showError('Пожалуйста, введите название класса');
            return;
        }

        this.showLoading(true);
        this.hideError();

        try {
            const response = await fetch(`${this.apiUrl}/classes/name/${encodeURIComponent(className)}`);
            const data = await response.json();

            if (data.success) {
                this.currentClass = data.data;
                this.displayClass(data.data);
                // Переключаемся на вкладку классов, если она не активна
                if (this.activeTab !== 'classes') {
                    document.getElementById('classes-tab').click();
                }
            } else {
                this.showError(data.message || 'Класс не найден');
                this.clearClassDisplay();
            }
        } catch (error) {
            console.error('Error searching class:', error);
            this.showError('Ошибка при поиске класса. Проверьте соединение с сервером.');
        } finally {
            this.showLoading(false);
        }
    }

    displayClass(classData) {
        const container = document.getElementById('class-container');
        container.innerHTML = `
            <div class="class-header">
                <h2 class="class-name">${classData.className}</h2>
                <p class="academic-year">Учебный год: ${classData.academicYear}</p>
            </div>

            ${this.renderClassTeacher(classData)}
            ${this.renderStudentsSection(classData)}
            ${this.renderTeachersSection(classData)}
        `;
    }

    createClass() {
        this.showError('Функция создания класса будет реализована в будущем');
    }

    clearClassDisplay() {
        document.getElementById('class-container').innerHTML = `
            <p class="info-message">Введите название класса в поле поиска для отображения информации</p>
        `;
    }

    showLoading(show) {
        document.getElementById('loading').classList.toggle('hidden', !show);
    }

    showError(message) {
        const errorElement = document.getElementById('error-message');
        errorElement.textContent = message;
        errorElement.classList.remove('hidden');

        // Автоматическое скрытие ошибки через 5 секунд
        setTimeout(() => {
            errorElement.classList.add('hidden');
        }, 5000);
    }

    hideError() {
        document.getElementById('error-message').classList.add('hidden');
    }

    renderClassTeacher(classData) {
        if (!classData.classTeacher) {
            return `
                <div class="class-teacher">
                    <div class="teacher-title">📚 Классный руководитель не назначен</div>
                </div>
            `;
        }

        const teacher = classData.classTeacher;
        return `
            <div class="class-teacher">
                <div class="teacher-title">📚 Классный руководитель</div>
                <div class="teacher-card">
                    <div class="teacher-name">${teacher.fullName}</div>
                    <div class="teacher-info">Предмет: ${teacher.subject}</div>
                    <div class="contact-info">
                        <div class="contact-item">📧 ${teacher.email}</div>
                    </div>
                </div>
            </div>
        `;
    }

    renderStudentsSection(classData) {
        if (!classData.students || classData.students.length === 0) {
            return `
                <div class="students-section">
                    <h3 class="section-title">👥 Список учеников</h3>
                    <p>В этом классе пока нет учеников</p>
                </div>
            `;
        }

        const studentsHTML = classData.students.map(student => `
            <div class="student-card">
                <div class="student-name">${student.fullName}</div>
                <div class="student-info">📧 ${student.email || 'Не указано'}</div>
                <div class="student-info">📱 ${student.phone || 'Не указано'}</div>
            </div>
        `).join('');

        return `
            <div class="students-section">
                <h3 class="section-title">👥 Список учеников (${classData.students.length})</h3>
                <div class="students-grid">
                    ${studentsHTML}
                </div>
            </div>
        `;
    }

    renderTeachersSection(classData) {
        if (!classData.teachers || classData.teachers.length === 0) {
            return `
                <div class="teachers-section">
                    <h3 class="section-title">👩‍🏫 Предметные учителя</h3>
                    <p>Учителя для этого класса не назначены</p>
                </div>
            `;
        }

        const teachersHTML = classData.teachers.map(teacher => `
            <div class="teacher-card">
                <div class="teacher-name">${teacher.fullName}</div>
                <div class="teacher-info">Предмет: ${teacher.subject}</div>
                <div class="contact-info">
                    <div class="contact-item">📧 ${teacher.email}</div>
                </div>
            </div>
        `).join('');

        return `
            <div class="teachers-section">
                <h3 class="section-title">👩‍🏫 Предметные учителя (${classData.teachers.length})</h3>
                <div class="teachers-grid">
                    ${teachersHTML}
                </div>
            </div>
        `;
    }
}

// Инициализация приложения при загрузке страницы
document.addEventListener('DOMContentLoaded', () => {
    window.schoolSystem = new SchoolSystem();
});