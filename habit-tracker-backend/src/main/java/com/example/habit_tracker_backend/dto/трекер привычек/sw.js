const CACHE_NAME = 'habit-tracker-v1';
const urlsToCache = [
    '/',
    '/index.html',
    '/loginStyle.css',
    '/mainScreenStyle.css',
    '/progressStyle.css',
    '/main.js',
    '/progress.js',
    '/script.js',
    '/navigation.js',
    '/android-chrome-192x192.png',
    '/android-chrome-512x512.png'
];

self.addEventListener('install', event => {
    event.waitUntil(
        caches.open(CACHE_NAME)
            .then(cache => cache.addAll(urlsToCache))
    );
});

self.addEventListener('fetch', event => {
    event.respondWith(
        caches.match(event.request)
            .then(response => response || fetch(event.request))
    );
});