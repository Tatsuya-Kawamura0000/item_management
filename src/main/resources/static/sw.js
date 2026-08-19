const CACHE_NAME = "item-management-static-v1";
const STATIC_PATH_PREFIXES = [
    "/css/",
    "/js/",
    "/icons/",
    "/bootstrap-datepicker-1.9.0-dist/"
];
const STATIC_FILES = new Set(["/manifest.webmanifest"]);

self.addEventListener("install", () => {
    // Do not call skipWaiting(): active pages move to a new worker naturally.
});

self.addEventListener("activate", (event) => {
    event.waitUntil(
        caches.keys().then((cacheNames) =>
            Promise.all(
                cacheNames
                    .filter((cacheName) => cacheName.startsWith("item-management-static-") && cacheName !== CACHE_NAME)
                    .map((cacheName) => caches.delete(cacheName))
            )
        )
    );
});

self.addEventListener("fetch", (event) => {
    const request = event.request;

    if (request.method !== "GET") {
        return;
    }

    const url = new URL(request.url);
    if (url.origin !== self.location.origin || !isStaticResource(url.pathname)) {
        return;
    }

    event.respondWith(networkFirstStaticResource(request));
});

function isStaticResource(pathname) {
    return STATIC_FILES.has(pathname) || STATIC_PATH_PREFIXES.some((prefix) => pathname.startsWith(prefix));
}

async function networkFirstStaticResource(request) {
    const cache = await caches.open(CACHE_NAME);

    try {
        const response = await fetch(request);
        if (response.ok) {
            await cache.put(request, response.clone());
        }
        return response;
    } catch (error) {
        const cachedResponse = await cache.match(request);
        if (cachedResponse) {
            return cachedResponse;
        }
        throw error;
    }
}
