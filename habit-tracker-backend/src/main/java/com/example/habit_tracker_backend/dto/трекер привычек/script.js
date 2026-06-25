// 1. Функция переключения вкладок (Вход / Регистрация)
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

// 2. Логика обработки отправки формы авторизации
document.addEventListener('DOMContentLoaded', () => {
    const authForm = document.getElementById('authForm');
    if (!authForm) return;

    authForm.addEventListener('submit', function(event) {
        event.preventDefault(); // Предотвращаем перезагрузку страницы

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
            // --- РЕЖИМ РЕГИСТРАЦИИ ---
            const nameValue = document.getElementById('name').value.trim();
            if (!nameValue) {
                alert('Пожалуйста, введите ваше имя!');
                return;
            }
            if (passwordValue !== confirmPasswordValue) {
                alert('Пароли не совпадают!');
                return;
            }

            const userExists = localStorage.getItem(`user_${loginValue}`);
            if (userExists) {
                alert('Пользователь с таким логином уже зарегистрирован!');
            } else {
                localStorage.setItem(`user_${loginValue}`, passwordValue);
                alert('Регистрация успешна! Теперь вы можете войти.');
                switchMode('login');
            }

        } else {
            // --- РЕЖИМ ВХОДА ---
            const savedPassword = localStorage.getItem(`user_${loginValue}`);

            if (savedPassword === null) {
                alert('Пользователь с таким логином не найден!');
            } else if (savedPassword !== passwordValue) {
                alert('Неверный пароль!');
            } else {
                // УСПЕШНЫЙ ВХОД:
                // 1. Проверяем, доступна ли глобальная функция переключения экранов
                if (typeof switchScreen === 'function') {
                    switchScreen(1); // Листаем на экран 1 (Кот-Босс)
                } else {
                    // Резервный вариант, если навигация выдала сбой
                    console.warn("Функция switchScreen не найдена. Переключаем display вручную.");
                    const bossPage = document.getElementById('bossPage');
                    if (container) container.style.display = 'none';
                    if (bossPage) bossPage.style.display = 'flex';
                }
            }
        }
    });

    // Нажатие на кнопку "дальше" на экране Босса
    const goToMainBtn = document.getElementById('goToMainBtn');
    if (goToMainBtn) {
        goToMainBtn.addEventListener('click', () => {
            if (typeof switchScreen === 'function') {
                switchScreen(2); // Листаем на экран 2 (Календарь)
            } else {
                const bossPage = document.getElementById('bossPage');
                const mainCont = document.querySelector('.main-container');
                if (bossPage) bossPage.style.display = 'none';
                if (mainCont) mainCont.style.display = 'flex';
            }
        });
    }
});