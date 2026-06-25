let currentScreenIndex = 0;
const totalScreens = 4;
let isTransitioning = false; // Защита от слишком быстрого пролистывания кучи экранов сразу

document.addEventListener('DOMContentLoaded', () => {
    const slider = document.getElementById('appSlider');
    if (!slider) return;

    // Инициализация стартового экрана
    switchScreen(0);

    // Делаем кружочки сверху кликабельными
    const dots = document.querySelectorAll('.header-circles .circle-dot');
    dots.forEach((dot, idx) => {
        dot.style.cursor = 'pointer';
        dot.addEventListener('click', () => switchScreen(idx));
    });

    let touchStartX = 0;
    let touchEndX = 0;
    let isDragging = false;

    // 1. Свайпы для мобильных устройств (тач)
    slider.addEventListener('touchstart', e => {
        touchStartX = e.changedTouches[0].clientX;
    }, {passive: true});

    slider.addEventListener('touchend', e => {
        touchEndX = e.changedTouches[0].clientX;
        handleSwipe(touchStartX - touchEndX);
    }, {passive: true});


    // 2. Зажатие и перетаскивание ОДНИМ пальцем/мышкой (для тестов)
    slider.addEventListener('mousedown', e => {
        touchStartX = e.clientX;
        isDragging = true;
    });

    slider.addEventListener('mouseup', e => {
        if (!isDragging) return;
        touchEndX = e.clientX;
        isDragging = false;
        handleSwipe(touchStartX - touchEndX);
    });

    slider.addEventListener('mouseleave', () => { isDragging = false; });


    // 3. МАГИЯ ДЛЯ ТАЧПАДА НОУТБУКА (Два пальца влево/вправо)
    let wheelAccumulator = 0; // Накапливает микродвижения тачпада
    let wheelTimeout;

    slider.addEventListener('wheel', e => {
        // Проверяем, что движение идет по горизонтали (deltaX)
        if (Math.abs(e.deltaX) > Math.abs(e.deltaY)) {
            e.preventDefault(); // Запрещаем браузеру пытаться кидать нас назад по истории страниц
            
            if (isTransitioning) return; // Если экран уже в процессе анимации — игнорируем

            wheelAccumulator += e.deltaX;

            clearTimeout(wheelTimeout);
            wheelTimeout = setTimeout(() => {
                wheelAccumulator = 0; // Сбрасываем, если пользователь перестал вести пальцами
            }, 150);

            // Если накопился сильный жест (порог 50)
            if (Math.abs(wheelAccumulator) > 50) {
                handleSwipe(wheelAccumulator);
                wheelAccumulator = 0; // Сразу сбрасываем после успешного свайпа
            }
        }
    }, { passive: false }); // Важно для e.preventDefault()


    // Общая функция обработки направления сдвига
    function handleSwipe(diff) {
        const swipeThreshold = 30; // Чувствительность

        if (Math.abs(diff) > swipeThreshold) {
            if (diff > 0 && currentScreenIndex < totalScreens - 1) {
                switchScreen(currentScreenIndex + 1);
            } else if (diff < 0 && currentScreenIndex > 0) {
                switchScreen(currentScreenIndex - 1);
            }
        }
    }
});

function switchScreen(index) {
    currentScreenIndex = index;
    const slider = document.getElementById('appSlider');
    if (!slider) return;

    isTransitioning = true;
    slider.style.transform = `translateX(-${index * 25}%)`;
    
    // Включаем таймаут на время анимации (0.4 секунды в CSS), чтобы экраны не улетали со свистом
    setTimeout(() => {
        isTransitioning = false;
    }, 400);

    const dots = document.querySelectorAll('.header-circles .circle-dot');
    dots.forEach((dot, idx) => {
        if (idx === index) {
            dot.classList.add('active');
        } else {
            dot.classList.remove('active');
        }
    });

    // Обновление контента
    if (index === 2 && typeof initCalendar === 'function') {
        initCalendar();
        if (typeof renderHabits === 'function') renderHabits();
    }
    if (index === 3 && typeof renderProgressPage === 'function') {
        renderProgressPage();
    }

    // ИСПРАВЛЕНО: Показываем плюсик ТОЛЬКО на экране Календаря (индекс 2)
    const plusBtn = document.getElementById('openModalBtn');
    if (plusBtn) {
        if (index === 2) {
            plusBtn.style.display = 'flex';
        } else {
            plusBtn.style.display = 'none';
        }
    }
}