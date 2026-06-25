// ===== URL для связи с бекендом =====
const API_URL = 'https://63e5ebe1-0ee9-4762-a0cb-9cb2fa454c06.tunnel4.com/api';
// ====================================

// Константы месяцев на русском
const MONTHS_RU = ["ЯНВАРЬ", "ФЕВРАЛЬ", "МАРТ", "АПРЕЛЬ", "МАЙ", "ИЮНЬ", "ИЮЛЬ", "АВГУСТ", "СЕНТЯБРЬ", "ОКТЯБРЬ", "НОЯБРЬ", "ДЕКАБРЬ"];

// Состояние приложения
let currentDate = new Date();
let selectedDate = new Date();
let habits = JSON.parse(localStorage.getItem('user_habits')) || [];

// ===== ИНИЦИАЛИЗАЦИЯ =====
document.addEventListener('DOMContentLoaded', () => {
    console.log('=== main.js загружен ===');
    initCalendar();
    renderHabits();
    setupModalEvents();

    document.getElementById('prevMonthBtn').addEventListener('click', () => changeMonth(-1));
    document.getElementById('nextMonthBtn').addEventListener('click', () => changeMonth(1));
});

// ===== КАЛЕНДАРЬ =====
function initCalendar() {
    const year = currentDate.getFullYear();
    const month = currentDate.getMonth();

    document.getElementById('monthTitle').innerText = MONTHS_RU[month];

    const container = document.getElementById('calendarDays');
    container.innerHTML = '';

    const firstDayIndex = new Date(year, month, 1).getDay(); 
    const startOffset = firstDayIndex === 0 ? 6 : firstDayIndex - 1;
    const totalDays = new Date(year, month + 1, 0).getDate();
    const realToday = new Date();

    // Дни предыдущего месяца
    const prevMonthTotalDays = new Date(year, month, 0).getDate();
    for (let i = startOffset - 1; i >= 0; i--) {
        const dayCell = document.createElement('div');
        dayCell.className = 'calendar-day-cell neighbor-month-day';
        dayCell.innerText = prevMonthTotalDays - i;
        const cellDate = new Date(year, month - 1, prevMonthTotalDays - i);
        setupDayClick(dayCell, cellDate, realToday);
        container.appendChild(dayCell);
    }

    // Дни текущего месяца
    for (let day = 1; day <= totalDays; day++) {
        const dayCell = document.createElement('div');
        dayCell.className = 'calendar-day-cell';
        dayCell.innerText = day;
        const cellDate = new Date(year, month, day);
        setupDayClick(dayCell, cellDate, realToday);
        container.appendChild(dayCell);
    }

    // Дни следующего месяца
    const totalCellsRendered = startOffset + totalDays;
    const remainingCells = 42 - totalCellsRendered;
    for (let day = 1; day <= remainingCells; day++) {
        const dayCell = document.createElement('div');
        dayCell.className = 'calendar-day-cell neighbor-month-day';
        dayCell.innerText = day;
        const cellDate = new Date(year, month + 1, day);
        setupDayClick(dayCell, cellDate, realToday);
        container.appendChild(dayCell);
    }
}

function changeMonth(direction) {
    const currentMonth = currentDate.getMonth();
    currentDate.setMonth(currentMonth + direction);
    initCalendar();
}

function setupDayClick(element, cellDate, realToday) {
    const isToday = cellDate.getDate() === realToday.getDate() &&
                    cellDate.getMonth() === realToday.getMonth() &&
                    cellDate.getFullYear() === realToday.getFullYear();

    const isSelected = cellDate.getDate() === selectedDate.getDate() &&
                       cellDate.getMonth() === selectedDate.getMonth() &&
                       cellDate.getFullYear() === selectedDate.getFullYear();

    if (isToday) element.classList.add('current-day');
    if (isSelected) element.classList.add('selected-day');

    element.onclick = function() {
        selectedDate = cellDate;
        initCalendar();
        renderHabits();
    };
}

function toggleDaysSelection() {
    const frequency = document.getElementById('habitFrequency').value;
    const daysGroup = document.getElementById('daysCheckboxesGroup');
    if (!daysGroup) return;
    daysGroup.style.display = frequency === 'specific' ? 'flex' : 'none';
}

// ===== МОДАЛЬНОЕ ОКНО =====
function setupModalEvents() {
    const overlay = document.getElementById('habitModal'); 
    if (!overlay) return;
    
    document.getElementById('openModalBtn').addEventListener('click', () => {
        overlay.style.display = 'flex';
        overlay.classList.add('active');
    });
    
    document.getElementById('closeModalBtn').addEventListener('click', () => {
        overlay.style.display = 'none';
        overlay.classList.remove('active');
        document.getElementById('habitForm').reset();
        toggleDaysSelection();
    });

    document.getElementById('habitForm').addEventListener('submit', (e) => {
        e.preventDefault();
        
        const name = document.getElementById('habitName').value.trim();
        const frequency = document.getElementById('habitFrequency').value;
        const color = document.querySelector('input[name="habitColor"]:checked').value;
        
        let selectedDays = [];
        if (frequency === 'specific') {
            const checkedBoxes = document.querySelectorAll('#daysCheckboxesGroup input[type="checkbox"]:checked');
            checkedBoxes.forEach(box => selectedDays.push(parseInt(box.value)));
        }

        const userId = localStorage.getItem('userId');
        if (!userId) {
            alert('Ошибка: пользователь не авторизован');
            return;
        }

        fetch(`${API_URL}/habits`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                userId: parseInt(userId),
                title: name,
                description: '',
                color: color,
                frequencyType: frequency === 'everyday' ? 'daily' : 'weekly',
                frequencyDays: frequency === 'specific' ? selectedDays : null,
                plantType: 1,
                plantStage: 0
            })
        })
        .then(response => {
            if (!response.ok) throw new Error('Ошибка сервера');
            return response.json();
        })
        .then(() => {
            overlay.style.display = 'none';
            overlay.classList.remove('active');
            document.getElementById('habitForm').reset();
            toggleDaysSelection();
            loadHabitsFromAPI();
        })
        .catch(error => {
            console.error('Ошибка:', error);
            alert('Не удалось создать привычку.');
        });
    });
}

// ===== ЗАГРУЗКА ПРИВЫЧЕК С СЕРВЕРА =====
function loadHabitsFromAPI() {
    const userId = localStorage.getItem('userId');
    if (!userId) return;

    fetch(`${API_URL}/habits/user/${userId}`)
        .then(response => {
            if (!response.ok) throw new Error('Ошибка загрузки');
            return response.json();
        })
        .then(data => {
            habits = data.map(habit => ({
                id: habit.id.toString(),
                name: habit.title,
                frequency: habit.frequencyType === 'daily' ? 'everyday' : 'specific',
                days: habit.frequencyDays || [],
                color: habit.color || '#5cc1e0',
                completedDates: []
            }));
            localStorage.setItem('user_habits', JSON.stringify(habits));
            renderHabits();
        })
        .catch(error => {
            console.error('Ошибка загрузки привычек:', error);
        });
}

function saveToDB() {
    localStorage.setItem('user_habits', JSON.stringify(habits));
}

// ===== ОТРИСОВКА ПРИВЫЧЕК =====
function renderHabits() {
    const uncompletedContainer = document.getElementById('uncompletedHabitsList');
    const completedContainer = document.getElementById('completedHabitsList');
    const divider = document.getElementById('habitsDivider');

    uncompletedContainer.innerHTML = '';
    completedContainer.innerHTML = '';

    const offset = selectedDate.getTimezoneOffset();
    const localSelected = new Date(selectedDate.getTime() - (offset * 60 * 1000));
    const todayStr = localSelected.toISOString().split('T')[0]; 
    const currentDayOfWeek = selectedDate.getDay();

    const todaysHabits = habits.filter(habit => {
        if (habit.frequency === 'everyday') return true;
        if (habit.frequency === 'specific') {
            return habit.days.includes(currentDayOfWeek);
        }
        return false;
    });

    let hasCompleted = false;

    todaysHabits.forEach(habit => {
        const isDoneToday = habit.completedDates.includes(todayStr);

        const habitHtml = document.createElement('div');
        habitHtml.className = `habit-item ${isDoneToday ? 'completed' : ''}`;
        habitHtml.innerHTML = `
            <div class="habit-left">
                <div class="habit-checkbox ${isDoneToday ? 'checked' : ''}" 
                     style="border-color: ${habit.color}; ${isDoneToday ? `background-color: ${habit.color}` : ''}"
                     onclick="toggleHabitStatus('${habit.id}', '${todayStr}')">
                </div>
                <span class="habit-text">${habit.name}</span>
            </div>
            <a class="reminder-link">Добавить напоминание</a>
        `;

        if (isDoneToday) {
            completedContainer.appendChild(habitHtml);
            hasCompleted = true;
        } else {
            uncompletedContainer.appendChild(habitHtml);
        }
    });

    divider.style.display = hasCompleted ? 'block' : 'none';
}

function toggleHabitStatus(habitId, dateStr) {
    const habit = habits.find(h => h.id === habitId);
    if (!habit) return;

    const index = habit.completedDates.indexOf(dateStr);
    if (index > -1) {
        habit.completedDates.splice(index, 1);
    } else {
        habit.completedDates.push(dateStr);
    }

    saveToDB();
    renderHabits();
}

// ===== ПЕРЕКЛЮЧЕНИЕ ВКЛАДОК =====
function switchMode(mode) {
    const container = document.getElementById('authContainer');
    const tabLogin = document.getElementById('tabLogin');
    const tabRegister = document.getElementById('tabRegister');
    const submitBtn = document.getElementById('submitBtn');

    if (!container || !tabLogin || !tabRegister || !submitBtn) return;

    if (mode === 'register') {
        container.classList.add('register-mode');
        tabRegister.classList.add('active');
        tabLogin.classList.remove('active');
        submitBtn.innerHTML = 'регистрация &rarr;'; 
    } else {
        container.classList.remove('register-mode');
        tabLogin.classList.add('active');
        tabRegister.classList.remove('active');
        submitBtn.innerHTML = 'войти &rarr;';
    }
}

// ===== ОБРАБОТКА ФОРМЫ АВТОРИЗАЦИИ =====
document.addEventListener('DOMContentLoaded', () => {
    const authForm = document.getElementById('authForm');
    if (!authForm) return;

    authForm.addEventListener('submit', function(event) {
        event.preventDefault();

        authForm.addEventListener('submit', function(event) {
    event.preventDefault();
    console.log('=== ФОРМА ОТПРАВЛЕНА ===');
    console.log('Режим:', document.getElementById('authContainer').classList.contains('register-mode') ? 'регистрация' : 'вход');
    // ... остальной код
});

        const loginValue = document.getElementById('login').value.trim();
        const passwordValue = document.getElementById('password').value;
        const confirmPasswordValue = document.getElementById('confirmPassword').value;
        const container = document.getElementById('authContainer');
        
        if (!container) return;
        const isRegisterMode = container.classList.contains('register-mode');

        if (!loginValue || !passwordValue) {
            alert('Пожалуйста, заполните все обязательные поля!');
            return;
        }

        if (isRegisterMode) {
            const nameValue = document.getElementById('name').value.trim();
            if (!nameValue) {
                alert('Пожалуйста, введите ваше имя!');
                return;
            }
            if (passwordValue !== confirmPasswordValue) {
                alert('Пароли не совпадают!');
                return;
            }

            console.log('=== РЕГИСТРАЦИЯ ===');
            console.log('Логин:', loginValue);

            fetch(`${API_URL}/auth/register`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    username: loginValue,
                    fullName: nameValue,
                    password: passwordValue,
                    confirmPassword: confirmPasswordValue
                })
            })
            .then(response => {
                console.log('Статус:', response.status);
                return response.json();
            })
            .then(data => {
                console.log('Ответ:', data);
                if (data.success) {
                    alert('Регистрация успешна! Теперь вы можете войти.');
                    switchMode('login');
                } else {
                    alert(data.message || 'Ошибка регистрации');
                }
            })
            .catch(error => {
                console.error('Ошибка:', error);
                alert('Не удалось зарегистрироваться.');
            });

        } else {
            console.log('=== ВХОД ===');
            console.log('Логин:', loginValue);

            fetch(`${API_URL}/auth/login`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    username: loginValue,
                    password: passwordValue
                })
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    localStorage.setItem('userId', data.userId);
                    localStorage.setItem('username', data.username);
                    localStorage.setItem('fullName', data.fullName);
                    loadHabitsFromAPI();
                    if (typeof switchScreen === 'function') {
                        switchScreen(1);
                    } else {
                        const container = document.getElementById('authContainer');
                        const bossPage = document.getElementById('bossPage');
                        if (container) container.style.display = 'none';
                        if (bossPage) bossPage.style.display = 'flex';
                    }
                } else {
                    alert(data.message || 'Неверный логин или пароль');
                }
            })
            .catch(error => {
                console.error('Ошибка:', error);
                alert('Не удалось войти.');
            });
        }
    });

    const goToMainBtn = document.getElementById('goToMainBtn');
    if (goToMainBtn) {
        goToMainBtn.addEventListener('click', () => {
            if (typeof switchScreen === 'function') {
                switchScreen(2);
            } else {
                const bossPage = document.getElementById('bossPage');
                const mainCont = document.querySelector('.main-container');
                if (bossPage) bossPage.style.display = 'none';
                if (mainCont) mainCont.style.display = 'flex';
            }
        });
    }
});