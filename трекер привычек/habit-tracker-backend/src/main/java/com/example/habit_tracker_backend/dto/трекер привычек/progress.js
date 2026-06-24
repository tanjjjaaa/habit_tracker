document.addEventListener('DOMContentLoaded', () => {
    renderProgressPage();

    // Перехватываем кнопку назад (если она у вас есть), чтобы она просто свайпала обратно на календарь
    const backBtn = document.getElementById('backToMainBtn');
    if (backBtn) {
        backBtn.addEventListener('click', () => {
            if (typeof switchScreen === 'function') switchScreen(2);
        });
    }
});

function renderProgressPage() {
    const container = document.getElementById('progressHabitsList');
    container.innerHTML = '';

    // База данных привычек из localStorage
    const habits = JSON.parse(localStorage.getItem('user_habits')) || [];
    
    // Определяем текущие временные рамки (текущий месяц и год)
    const now = new Date();
    const year = now.getFullYear();
    const month = now.getMonth();
    
    // Количество дней в этом месяце (30, 31 и т.д.)
    const totalDaysInMonth = new Date(year, month + 1, 0).getDate();

    if (habits.length === 0) {
        container.innerHTML = `<p style="color: #a0a0a0; text-align: center;">У вас пока нет добавленных привычек.</p>`;
        return;
    }

    habits.forEach(habit => {
        // Создаем контейнер строки привычки
        const rowElement = document.createElement('div');
        rowElement.className = 'progress-habit-row';

        const coreDiv = document.createElement('div');
        coreDiv.className = 'habit-row-core';

        // Имя привычки
        const label = document.createElement('div');
        label.className = 'habit-name-label';
        label.innerText = habit.name;
        coreDiv.appendChild(label);

        // Контейнер кружочков
        const circlesGrid = document.createElement('div');
        circlesGrid.className = 'circles-grid';

        // ГЕНЕРАЦИЯ КРУЖОЧКОВ НА ОСНОВЕ ПЛАНА МЕСЯЦА
        for (let d = 1; d <= totalDaysInMonth; d++) {
            const iterDate = new Date(year, month, d);
            const dayOfWeek = iterDate.getDay(); // 0 (Вс) - 6 (Сб)
            
            // Форматируем дату в строку "YYYY-MM-DD" для сверки с выполнением
            const offset = iterDate.getTimezoneOffset();
            const localIterDate = new Date(iterDate.getTime() - (offset * 60 * 1000));
            const dateStr = localIterDate.toISOString().split('T')[0];

            // Проверяем, должна ли привычка выполняться в этот день 'd'
            let isScheduled = false;
            if (habit.frequency === 'everyday') {
                isScheduled = true;
            } else if (habit.frequency === 'specific') {
                isScheduled = habit.days.includes(dayOfWeek);
            }

            // Если в этот день привычка запланирована — рисуем для неё кружок
            if (isScheduled) {
                const dot = document.createElement('div');
                dot.className = 'progress-dot';

                // Если дата есть в массиве выполненных — закрашиваем бирюзовым
                if (habit.completedDates && habit.completedDates.includes(dateStr)) {
                    dot.classList.add('filled');
                }

                circlesGrid.appendChild(dot);
            }
        }

        coreDiv.appendChild(circlesGrid);
        rowElement.appendChild(coreDiv);

        // Добавляем фирменную толстую серую черту снизу
        const divider = document.createElement('div');
        divider.className = 'progress-divider';
        rowElement.appendChild(divider);

        container.appendChild(rowElement);
    });
}