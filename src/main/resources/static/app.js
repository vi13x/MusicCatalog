const endpoints = {
    artists: "/api/artists",
    albums: "/api/albums",
    tracks: "/api/tracks",
    genres: "/api/genres",
    playlists: "/api/playlists"
};

const navItems = [
    { key: "catalog", label: "Каталог", resource: "albums" },
    { key: "albums", label: "Альбомы", resource: "albums" },
    { key: "artists", label: "Исполнители", resource: "artists" },
    { key: "tracks", label: "Треки", resource: "tracks" },
    { key: "genres", label: "Жанры", resource: "genres" },
    { key: "playlists", label: "Плейлисты", resource: "playlists" }
];

const state = {
    active: "catalog",
    loading: false,
    error: "",
    editing: null,
    detailModal: null,
    previewTrackId: null,
    localFilters: {},
    pagination: {
        albums: 1,
        artists: 1,
        tracks: 1,
        genres: 1,
        playlists: 1
    },
    albumSearch: defaultAlbumSearch(),
    data: {
        artists: [],
        albums: [],
        tracks: [],
        genres: [],
        playlists: []
    }
};

const resourceLabels = {
    albums: { title: "Альбомы", singular: "альбом" },
    artists: { title: "Исполнители", singular: "исполнителя" },
    tracks: { title: "Треки", singular: "трек" },
    genres: { title: "Жанры", singular: "жанр" },
    playlists: { title: "Плейлисты", singular: "плейлист" }
};

const PAGE_SIZE = 12;
const INLINE_LIST_PREVIEW_LENGTH = 52;
const DEMO_TRACK_PREVIEWS = {
    "blinding lights": "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
    "get lucky": "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
    "bohemian rhapsody": "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3"
};

let toastTimer = null;
let previewAudio = null;
const themeToggle = document.getElementById("theme-toggle");

initTheme();

themeToggle?.addEventListener("change", (event) => {
    setTheme(event.target.checked ? "light" : "dark");
});

document.addEventListener("click", (event) => {
    handleClick(event).catch(handleAsyncError);
});

document.addEventListener("submit", (event) => {
    handleSubmit(event).catch(handleAsyncError);
});

document.addEventListener("input", (event) => {
    handleInput(event).catch(handleAsyncError);
});

document.addEventListener("keydown", handleKeydown);

window.addEventListener("hashchange", syncRoute);

syncRoute();
loadAll({ resetAlbumSearch: true }).catch(handleAsyncError);

function initTheme() {
    const savedTheme = localStorage.getItem("musicCatalogTheme") || "dark";
    setTheme(savedTheme);
}

function setTheme(theme) {
    const normalizedTheme = theme === "light" ? "light" : "dark";
    document.documentElement.dataset.theme = normalizedTheme;
    localStorage.setItem("musicCatalogTheme", normalizedTheme);
    if (themeToggle) {
        themeToggle.checked = normalizedTheme === "light";
    }
}

function defaultAlbumSearch() {
    return {
        active: false,
        items: [],
        total: 0,
        params: {
            title: "",
            artistName: "",
            genreName: "",
            yearFrom: "",
            yearTo: ""
        }
    };
}

async function loadAll({ resetAlbumSearch = false } = {}) {
    state.loading = true;
    state.error = "";

    try {
        const entries = await Promise.all(
            Object.entries(endpoints).map(async ([resource, url]) => [resource, await requestJson(url)])
        );

        for (const [resource, items] of entries) {
            state.data[resource] = Array.isArray(items) ? items : [];
        }

        if (resetAlbumSearch) {
            state.albumSearch = defaultAlbumSearch();
        }
    } catch (error) {
        state.error = error.message;
        showToast(error.message);
    } finally {
        state.loading = false;
        render();
    }
}

async function requestJson(url, options = {}) {
    const response = await fetch(url, {
        headers: {
            Accept: "application/json",
            ...(options.body ? { "Content-Type": "application/json" } : {})
        },
        ...options
    });
    const text = await response.text();
    const body = parseJson(text);

    if (!response.ok) {
        throw new Error(formatApiError(body, response.status));
    }

    return body;
}

function parseJson(text) {
    if (!text) {
        return null;
    }
    try {
        return JSON.parse(text);
    } catch {
        return text;
    }
}

function formatApiError(body, status) {
    if (body && typeof body === "object") {
        const details = Array.isArray(body.validationErrors) && body.validationErrors.length > 0
            ? `: ${body.validationErrors.map((item) => `${item.field} ${item.message}`).join("; ")}`
            : "";
        return `${body.message || body.error || "Ошибка API"}${details}`;
    }
    return `Ошибка API: HTTP ${status}`;
}

function syncRoute() {
    const key = window.location.hash.replace(/^#\/?/, "") || "catalog";
    state.active = navItems.some((item) => item.key === key) ? key : "catalog";
    if (state.editing && state.editing.resource !== state.active) {
        state.editing = null;
    }
    render();
}

function render() {
    renderNav();

    const app = document.getElementById("app");
    if (!app) {
        return;
    }

    if (state.loading && !hasAnyData()) {
        app.innerHTML = `<p class="empty-state">Загрузка каталога</p>`;
        return;
    }

    const content = state.active === "catalog"
        ? renderCatalog()
        : renderResource(state.active);
    app.innerHTML = `${content}${renderEditorModal()}${renderDetailModal()}`;
}

function renderNav() {
    const nav = document.getElementById("nav");
    nav.innerHTML = navItems.map((item) => {
        const count = item.resource ? (state.data[item.resource] || []).length : "";
        return `
            <button class="nav-button ${item.key === state.active ? "active" : ""}" type="button" data-nav="${item.key}">
                <span>${escapeHtml(item.label)}</span>
                ${count !== "" ? `<span class="nav-count">${escapeHtml(count)}</span>` : ""}
            </button>
        `;
    }).join("");
}

function renderCatalog() {
    const albums = getFilteredAlbumsForCatalog();
    const featuredAlbums = albums.slice(0, 12);
    const featuredArtists = state.data.artists.slice(0, 8);
    const playlists = state.data.playlists.slice(0, 5);

    return `
        <section class="hero">
            <div>
                <p class="eyebrow">Моя музыка</p>
                <h2>Альбомы, треки и плейлисты под рукой</h2>
                <form id="catalog-search-form" class="search-bar">
                    <div class="field">
                        <label for="catalog-query">Поиск по каталогу</label>
                        <input id="catalog-query" name="query" value="${escapeAttr(state.localFilters.catalog || "")}" placeholder="Альбом, артист, жанр или трек">
                    </div>
                    ${state.localFilters.catalog ? `<button class="button secondary" type="button" data-action="clear-filter" data-resource="catalog">Сбросить</button>` : ""}
                </form>
            </div>
        </section>

        ${renderStats()}

        <section>
            <div class="section-heading">
                <h2>Альбомы</h2>
                <button class="button secondary" type="button" data-nav="albums">Открыть все</button>
            </div>
            ${featuredAlbums.length ? renderAlbumGrid(featuredAlbums) : `<p class="empty-state">Нет альбомов</p>`}
        </section>

        <section>
            <div class="section-heading">
                <h2>Исполнители</h2>
                <button class="button secondary" type="button" data-nav="artists">Открыть всех</button>
            </div>
            ${featuredArtists.length ? renderArtistGrid(featuredArtists) : `<p class="empty-state">Нет исполнителей</p>`}
        </section>

        <section>
            <div class="section-heading">
                <h2>Плейлисты</h2>
                <button class="button secondary" type="button" data-nav="playlists">Открыть все</button>
            </div>
            ${playlists.length ? renderPlaylistGrid(playlists) : `<p class="empty-state">Нет плейлистов</p>`}
        </section>
    `;
}

function renderStats() {
    const stats = [
        ["Исполнители", state.data.artists.length],
        ["Альбомы", state.data.albums.length],
        ["Треки", state.data.tracks.length],
        ["Жанры", state.data.genres.length],
        ["Плейлисты", state.data.playlists.length]
    ];

    return `
        <section class="stats" aria-label="Статистика каталога">
            ${stats.map(([label, value]) => `
                <div class="stat">
                    <strong>${escapeHtml(value)}</strong>
                    <span>${escapeHtml(label)}</span>
                </div>
            `).join("")}
        </section>
    `;
}

function renderResource(resource) {
    const labels = resourceLabels[resource];
    if (!labels) {
        return `<p class="empty-state">Раздел не найден</p>`;
    }

    const visibleItems = getVisibleItems(resource);
    const pagination = getPagination(resource, visibleItems.length);
    const items = visibleItems.slice(pagination.start, pagination.end);
    const sourceCount = getSourceItems(resource).length;
    const count = resource === "albums" && state.albumSearch.active
        ? `${visibleItems.length} / ${state.albumSearch.total}`
        : `${visibleItems.length} / ${sourceCount}`;

    return `
        <section class="workspace-grid">
            <div class="panel">
                <div class="panel-header">
                    <h2 class="panel-title">${escapeHtml(labels.title)} <span class="muted">${escapeHtml(count)}</span></h2>
                    <button class="button" type="button" data-action="new" data-resource="${resource}">Добавить</button>
                </div>
                <div class="panel-body">
                    ${resource === "albums" ? renderAlbumSearch() : ""}
                    ${renderLocalFilter(resource)}
                    ${renderResourceContent(resource, items, pagination)}
                    ${renderPagination(resource, pagination, visibleItems.length)}
                </div>
            </div>
        </section>
    `;
}

function renderAlbumSearch() {
    const params = state.albumSearch.params;
    return `
        <form id="album-search-form" class="album-search">
            <div class="field">
                <label for="album-search-title">Альбом</label>
                <input id="album-search-title" name="title" maxlength="160" value="${escapeAttr(params.title)}">
            </div>
            <div class="field">
                <label for="album-search-artist">Исполнитель</label>
                <input id="album-search-artist" name="artistName" maxlength="120" value="${escapeAttr(params.artistName)}">
            </div>
            <div class="field">
                <label for="album-search-genre">Жанр</label>
                <input id="album-search-genre" name="genreName" maxlength="80" value="${escapeAttr(params.genreName)}">
            </div>
            <div class="field">
                <label for="album-search-year-from">Год от</label>
                <input id="album-search-year-from" name="yearFrom" type="number" min="1900" max="2100" value="${escapeAttr(params.yearFrom)}">
            </div>
            <div class="field">
                <label for="album-search-year-to">Год до</label>
                <input id="album-search-year-to" name="yearTo" type="number" min="1900" max="2100" value="${escapeAttr(params.yearTo)}">
            </div>
            <div class="album-search-actions">
                <button class="button" type="submit">Найти</button>
                ${state.albumSearch.active ? `<button class="button secondary" type="button" data-action="clear-album-search">Сбросить</button>` : ""}
            </div>
        </form>
    `;
}

function renderLocalFilter(resource) {
    const value = state.localFilters[resource] || "";
    return `
        <form id="local-filter-form" class="toolbar" data-resource="${resource}">
            <div class="field">
                <label for="local-filter-input">Быстрый фильтр</label>
                <input id="local-filter-input" name="query" value="${escapeAttr(value)}" placeholder="Введите часть названия">
            </div>
            ${value ? `<button class="button secondary" type="button" data-action="clear-filter" data-resource="${resource}">Очистить</button>` : ""}
        </form>
    `;
}

function renderPagination(resource, pagination, totalItems) {
    if (pagination.totalPages <= 1) {
        return "";
    }

    const from = pagination.start + 1;
    const to = Math.min(pagination.end, totalItems);
    return `
        <div class="pagination" aria-label="Пагинация">
            <span class="pagination-info">Показано ${escapeHtml(from)}-${escapeHtml(to)} из ${escapeHtml(totalItems)}</span>
            <div class="pagination-buttons">
                <button class="page-button" type="button" data-action="page" data-resource="${resource}" data-page="${pagination.page - 1}" ${pagination.page <= 1 ? "disabled" : ""}>Назад</button>
                ${getPageButtons(pagination.page, pagination.totalPages).map((page) => page === "gap"
                    ? `<span class="page-gap">...</span>`
                    : `<button class="page-button ${page === pagination.page ? "active" : ""}" type="button" data-action="page" data-resource="${resource}" data-page="${page}">${page}</button>`
                ).join("")}
                <button class="page-button" type="button" data-action="page" data-resource="${resource}" data-page="${pagination.page + 1}" ${pagination.page >= pagination.totalPages ? "disabled" : ""}>Вперед</button>
            </div>
        </div>
    `;
}

function renderResourceContent(resource, items, pagination = null) {
    if (!items.length) {
        return `<p class="empty-state">Нет записей</p>`;
    }

    switch (resource) {
        case "albums":
            return renderAlbumGrid(items);
        case "artists":
            return renderArtistGrid(items);
        case "tracks":
            return renderTrackList(items, pagination?.start || 0);
        case "genres":
            return renderGenreGrid(items);
        case "playlists":
            return renderPlaylistGrid(items);
        default:
            return `<p class="empty-state">Раздел не найден</p>`;
    }
}

function renderAlbumGrid(albums) {
    return `
        <div class="grid album-grid">
            ${albums.map(renderAlbumCard).join("")}
        </div>
    `;
}

function renderAlbumCard(album) {
    const tracks = getAlbumTracks(album);
    return `
        <article class="album-card">
            <div class="cover" style="--hue: ${hashHue(album.title)}"><span>${escapeHtml(initials(album.title))}</span></div>
            <p class="card-topline">${escapeHtml(album.year || "")}</p>
            <div class="card-title">${escapeHtml(album.title)}</div>
            <p class="card-subtitle">${escapeHtml(getArtistName(album.artistId))}</p>
            ${renderInlineList("Жанры", asNumberArray(album.genreIds).map(getGenreName), {
                ownerResource: "albums",
                ownerId: album.id,
                listType: "genres",
                emptyText: "Нет жанров"
            })}
            ${renderInlineList("Треки", tracks.map((track) => track.title), {
                ownerResource: "albums",
                ownerId: album.id,
                listType: "tracks",
                emptyText: "Треки не добавлены"
            })}
            <div class="card-actions">
                ${renderActionButtons("albums", album.id)}
            </div>
        </article>
    `;
}

function renderArtistGrid(artists) {
    return `
        <div class="grid collection-grid">
            ${artists.map((artist) => {
                const albums = getAlbumsByArtist(artist.id);
                return `
                    <article class="collection-card">
                        <div class="cover" style="--hue: ${hashHue(artist.name)}"><span>${escapeHtml(initials(artist.name))}</span></div>
                        <div class="card-title">${escapeHtml(artist.name)}</div>
                        <p class="card-subtitle">${escapeHtml(albums.length)} альбом(ов)</p>
                        ${renderInlineList("Альбомы", albums.map((album) => album.title), {
                            ownerResource: "artists",
                            ownerId: artist.id,
                            listType: "albums",
                            emptyText: "Нет альбомов"
                        })}
                        <div class="card-actions">
                            ${renderActionButtons("artists", artist.id)}
                        </div>
                    </article>
                `;
            }).join("")}
        </div>
    `;
}

function renderGenreGrid(genres) {
    return `
        <div class="grid collection-grid">
            ${genres.map((genre) => {
                const albums = getAlbumsByGenre(genre.id);
                return `
                    <article class="collection-card">
                        <div class="cover" style="--hue: ${hashHue(genre.name)}"><span>${escapeHtml(initials(genre.name))}</span></div>
                        <div class="card-title">${escapeHtml(genre.name)}</div>
                        <p class="card-subtitle">${escapeHtml(albums.length)} альбом(ов)</p>
                        ${renderInlineList("Альбомы", albums.map((album) => album.title), {
                            ownerResource: "genres",
                            ownerId: genre.id,
                            listType: "albums",
                            emptyText: "Нет альбомов"
                        })}
                        <div class="card-actions">
                            ${renderActionButtons("genres", genre.id)}
                        </div>
                    </article>
                `;
            }).join("")}
        </div>
    `;
}

function renderPlaylistGrid(playlists) {
    return `
        <div class="grid collection-grid">
            ${playlists.map((playlist) => {
                const tracks = getTracksByIds(playlist.trackIds);
                return `
                    <article class="playlist-card">
                        <div class="cover" style="--hue: ${hashHue(playlist.name)}"><span>${escapeHtml(initials(playlist.name))}</span></div>
                        <div class="card-title">${escapeHtml(playlist.name)}</div>
                        <p class="card-subtitle">${escapeHtml(tracks.length)} трек(ов)</p>
                        ${renderInlineList("Треки", tracks.map((track) => track.title), {
                            ownerResource: "playlists",
                            ownerId: playlist.id,
                            listType: "tracks",
                            emptyText: "Треки не добавлены"
                        })}
                        <div class="card-actions">
                            ${renderActionButtons("playlists", playlist.id)}
                        </div>
                    </article>
                `;
            }).join("")}
        </div>
    `;
}

function renderTrackList(tracks, startIndex = 0) {
    return `
        <div class="track-list">
            ${tracks.map((track, index) => `
                <div class="track-row">
                    <span class="track-index">${String(startIndex + index + 1).padStart(2, "0")}</span>
                    <div>
                        <div class="track-title">${escapeHtml(track.title)}</div>
                        <div class="track-meta">${escapeHtml(getArtistForTrack(track))}</div>
                    </div>
                    <div class="track-album muted">${escapeHtml(getAlbumName(track.albumId))}</div>
                    <div>${renderTrackPreviewButton(track)}</div>
                    <div class="muted">${escapeHtml(formatDuration(track.durationSec))}</div>
                    <div class="row-actions">${renderActionButtons("tracks", track.id)}</div>
                </div>
            `).join("")}
        </div>
    `;
}

function renderTrackPreviewButton(track) {
    const previewUrl = getTrackPreviewUrl(track);
    if (!previewUrl) {
        return `<span class="muted">—</span>`;
    }
    const isPlaying = state.previewTrackId === Number(track.id);
    return `
        <button class="preview-button" type="button" data-action="preview-track" data-id="${escapeAttr(track.id)}">
            ${isPlaying ? "Пауза" : "▶ Прослушать"}
        </button>
    `;
}

function renderTrackPreview(tracks) {
    const preview = tracks.slice(0, 5);
    if (!preview.length) {
        return `<p class="muted">Треки не добавлены</p>`;
    }

    return `
        <div class="track-preview">
            ${preview.map((track) => `
                <div class="track-preview-row">
                    <span>${escapeHtml(track.title)}</span>
                    <span>${escapeHtml(formatDuration(track.durationSec))}</span>
                </div>
            `).join("")}
            ${tracks.length > preview.length ? `<div class="track-preview-row"><span>Еще ${tracks.length - preview.length}</span><span></span></div>` : ""}
        </div>
    `;
}

function renderInlineList(label, labels, { ownerResource, ownerId, listType, emptyText }) {
    const cleanLabels = labels.map(normalizeString).filter(Boolean);
    const text = cleanLabels.join(", ");

    if (!cleanLabels.length) {
        return `
            <div class="inline-list">
                <span class="inline-list-label">${escapeHtml(label)}</span>
                <span class="inline-list-text muted">${escapeHtml(emptyText)}</span>
            </div>
        `;
    }

    const canExpand = cleanLabels.length > 1 || text.length > INLINE_LIST_PREVIEW_LENGTH;
    return `
        <div class="inline-list">
            <span class="inline-list-label">${escapeHtml(label)}</span>
            <span class="inline-list-text" title="${escapeAttr(text)}">${escapeHtml(text)}</span>
            ${canExpand ? `
                <button class="inline-list-button" type="button" title="Показать весь список" aria-label="Показать весь список"
                        data-action="show-list" data-resource="${ownerResource}" data-id="${escapeAttr(ownerId)}" data-list="${listType}">...</button>
            ` : ""}
        </div>
    `;
}

function renderActionButtons(resource, id) {
    return `
        <button class="action-button icon-button" type="button" title="Редактировать" aria-label="Редактировать"
                data-action="edit" data-resource="${resource}" data-id="${escapeAttr(id)}">
            ${renderIcon("edit")}
            <span class="sr-only">Редактировать</span>
        </button>
        <button class="action-button icon-button danger" type="button" title="Удалить" aria-label="Удалить"
                data-action="delete" data-resource="${resource}" data-id="${escapeAttr(id)}">
            ${renderIcon("delete")}
            <span class="sr-only">Удалить</span>
        </button>
    `;
}

function renderIcon(name) {
    const icons = {
        edit: `<svg viewBox="0 0 24 24" aria-hidden="true"><path d="M4 20h4.6L19.3 9.3a2.1 2.1 0 0 0 0-3L17.7 4.7a2.1 2.1 0 0 0-3 0L4 15.4V20Zm11.8-13.9 2.1 2.1-1.5 1.5-2.1-2.1 1.5-1.5ZM6 16.2l6.9-6.9 2.1 2.1L8.2 18H6v-1.8Z"/></svg>`,
        delete: `<svg viewBox="0 0 24 24" aria-hidden="true"><path d="M8 21a2 2 0 0 1-2-2V8H5a1 1 0 0 1 0-2h4V5a2 2 0 0 1 2-2h2a2 2 0 0 1 2 2v1h4a1 1 0 1 1 0 2h-1v11a2 2 0 0 1-2 2H8Zm3-16v1h2V5h-2Zm-3 3v11h8V8H8Zm2 2h2v7h-2v-7Zm4 0h2v7h-2v-7Z"/></svg>`,
        close: `<svg viewBox="0 0 24 24" aria-hidden="true"><path d="M6.3 5 12 10.7 17.7 5 19 6.3 13.3 12 19 17.7 17.7 19 12 13.3 6.3 19 5 17.7 10.7 12 5 6.3 6.3 5Z"/></svg>`
    };
    return icons[name] || "";
}

function renderEditorModal() {
    if (!state.editing) {
        return "";
    }

    const resource = state.editing.resource;
    const item = getEditingItem(resource);
    const labels = resourceLabels[resource];
    const title = item ? `Редактировать ${labels.singular}` : `Создать ${labels.singular}`;

    return `
        <div class="modal-backdrop" data-modal-backdrop>
            <section class="entity-modal" role="dialog" aria-modal="true" aria-labelledby="entity-modal-title">
                <div class="modal-header">
                    <div>
                        <p class="eyebrow">${escapeHtml(labels.title)}</p>
                        <h2 id="entity-modal-title" class="panel-title">${escapeHtml(title)}</h2>
                    </div>
                    <button class="modal-close" type="button" data-action="cancel-edit" aria-label="Закрыть">
                        ${renderIcon("close")}
                    </button>
                </div>
                <div class="modal-body">
                    ${item ? renderItemSummary(resource, item) : ""}
                    ${renderForm(resource, item)}
                </div>
            </section>
        </div>
    `;
}

function renderDetailModal() {
    if (!state.detailModal) {
        return "";
    }

    return `
        <div class="modal-backdrop" data-modal-backdrop>
            <section class="list-modal" role="dialog" aria-modal="true" aria-labelledby="detail-modal-title">
                <div class="modal-header">
                    <h2 id="detail-modal-title" class="panel-title">${escapeHtml(state.detailModal.title)}</h2>
                    <button class="modal-close" type="button" data-action="close-detail" aria-label="Закрыть">
                        ${renderIcon("close")}
                    </button>
                </div>
                <div class="modal-body">
                    <ul class="full-list">
                        ${state.detailModal.items.map((item) => `<li>${escapeHtml(item)}</li>`).join("")}
                    </ul>
                </div>
            </section>
        </div>
    `;
}

function renderItemSummary(resource, item) {
    return `
        <div class="entity-summary">
            ${getItemSummaryRows(resource, item).map(([label, value]) => `
                <div>
                    <span>${escapeHtml(label)}</span>
                    <strong>${escapeHtml(value)}</strong>
                </div>
            `).join("")}
        </div>
    `;
}

function renderForm(resource, item) {
    switch (resource) {
        case "artists":
            return renderNamedForm(resource, item, "Имя исполнителя", "name", 120);
        case "genres":
            return renderNamedForm(resource, item, "Название жанра", "name", 80);
        case "albums":
            return renderAlbumForm(item);
        case "tracks":
            return renderTrackForm(item);
        case "playlists":
            return renderPlaylistForm(item);
        default:
            return `<p class="empty-state">Форма недоступна</p>`;
    }
}

function renderNamedForm(resource, item, label, name, maxLength) {
    return formShell(resource, item, `
        <div class="field">
            <label for="${resource}-${name}">${escapeHtml(label)}</label>
            <input id="${resource}-${name}" name="${name}" required maxlength="${maxLength}" value="${escapeAttr(item?.[name] || "")}">
        </div>
    `);
}

function renderAlbumForm(item) {
    const noArtists = state.data.artists.length === 0;
    const selectedGenres = item ? asNumberArray(item.genreIds) : [];
    const trackLines = (item ? getAlbumTracks(item) : [])
        .map((track) => `${track.title || ""} | ${track.durationSec || ""}`)
        .join("\n");

    return formShell("albums", item, `
        ${noArtists ? `<p class="empty-state">Сначала добавьте исполнителя</p>` : ""}
        <div class="field">
            <label for="album-title">Название альбома</label>
            <input id="album-title" name="title" required maxlength="160" value="${escapeAttr(item?.title || "")}">
        </div>
        <div class="field">
            <label for="album-year">Год</label>
            <input id="album-year" name="year" type="number" min="1900" max="2100" required value="${escapeAttr(item?.year || "")}">
        </div>
        <div class="field">
            <label for="album-artist">Исполнитель</label>
            <select id="album-artist" name="artistId" required>
                ${renderOptions(state.data.artists, item ? [item.artistId] : [], (artist) => artist.name)}
            </select>
        </div>
        <div class="field">
            <label for="album-genres">Жанры</label>
            <select id="album-genres" name="genreIds" multiple>
                ${renderOptions(state.data.genres, selectedGenres, (genre) => genre.name)}
            </select>
        </div>
        <div class="field">
            <label for="album-tracks">Треки</label>
            <textarea id="album-tracks" name="tracks" placeholder="Название | секунды">${escapeHtml(trackLines)}</textarea>
            <p class="field-hint">Одна строка на трек. Пример: Blinding Lights | 200</p>
        </div>
    `, { disabled: noArtists });
}

function renderTrackForm(item) {
    const noAlbums = state.data.albums.length === 0;
    const selectedAlbum = item ? [item.albumId] : [];
    const disableAlbum = item ? "disabled" : "";

    return formShell("tracks", item, `
        ${noAlbums ? `<p class="empty-state">Сначала добавьте альбом</p>` : ""}
        <div class="field">
            <label for="track-title">Название трека</label>
            <input id="track-title" name="title" required maxlength="160" value="${escapeAttr(item?.title || "")}">
        </div>
        <div class="field">
            <label for="track-duration">Длительность, сек.</label>
            <input id="track-duration" name="durationSec" type="number" min="1" required value="${escapeAttr(item?.durationSec || "")}">
        </div>
        <div class="field">
            <label for="track-album">Альбом</label>
            <select id="track-album" name="albumId" required ${disableAlbum}>
                ${renderOptions(state.data.albums, selectedAlbum, (album) => `${album.title} - ${getArtistName(album.artistId)}`)}
            </select>
            ${item ? `<p class="field-hint">Альбом у существующего трека не меняется.</p>` : ""}
        </div>
    `, { disabled: noAlbums });
}

function renderPlaylistForm(item) {
    return formShell("playlists", item, `
        <div class="field">
            <label for="playlist-name">Название плейлиста</label>
            <input id="playlist-name" name="name" required maxlength="120" value="${escapeAttr(item?.name || "")}">
        </div>
        <div class="field">
            <label for="playlist-tracks">Треки</label>
            <select id="playlist-tracks" name="trackIds" multiple>
                ${renderOptions(state.data.tracks, item ? asNumberArray(item.trackIds) : [], (track) => `${track.title} - ${getAlbumName(track.albumId)}`)}
            </select>
        </div>
    `);
}

function formShell(resource, item, fields, { disabled = false } = {}) {
    const submitLabel = item ? "Сохранить" : "Создать";
    return `
        <form class="entity-form" data-resource="${resource}" data-id="${escapeAttr(item?.id || "")}">
            ${fields}
            <div class="form-actions">
                <button class="button" type="submit" ${disabled ? "disabled" : ""}>${submitLabel}</button>
                <button class="button secondary" type="button" data-action="cancel-edit">Отмена</button>
            </div>
        </form>
    `;
}

function renderOptions(items, selectedIds, labelFactory) {
    const selected = new Set(asNumberArray(selectedIds));
    return items.map((item) => {
        const id = Number(item.id);
        return `<option value="${escapeAttr(id)}" ${selected.has(id) ? "selected" : ""}>${escapeHtml(labelFactory(item))}</option>`;
    }).join("");
}

async function handleClick(event) {
    if (event.target.matches("[data-modal-backdrop]")) {
        closeTopModal();
        render();
        return;
    }

    const navButton = event.target.closest("[data-nav]");
    if (navButton) {
        activate(navButton.dataset.nav);
        return;
    }

    const actionButton = event.target.closest("[data-action]");
    if (!actionButton) {
        return;
    }

    const { action, resource, id } = actionButton.dataset;

    if (action === "new") {
        state.detailModal = null;
        state.editing = { resource, id: null };
        render();
        return;
    }

    if (action === "edit") {
        state.detailModal = null;
        state.editing = { resource, id: Number(id) };
        if (state.active !== resource) {
            activate(resource, { keepEditing: true });
            return;
        }
        render();
        return;
    }

    if (action === "cancel-edit") {
        state.editing = null;
        render();
        return;
    }

    if (action === "close-detail") {
        state.detailModal = null;
        render();
        return;
    }

    if (action === "clear-filter") {
        state.localFilters[resource] = "";
        resetPage(resource);
        render();
        return;
    }

    if (action === "clear-album-search") {
        state.albumSearch = defaultAlbumSearch();
        resetPage("albums");
        render();
        return;
    }

    if (action === "page") {
        setPage(resource, Number(actionButton.dataset.page));
        render();
        return;
    }

    if (action === "show-list") {
        state.detailModal = getDetailModalData(resource, Number(id), actionButton.dataset.list);
        render();
        return;
    }

    if (action === "preview-track") {
        toggleTrackPreview(Number(id));
        render();
        return;
    }

    if (action === "delete") {
        await deleteResource(resource, Number(id));
    }
}

function toggleTrackPreview(trackId) {
    const track = getItem("tracks", trackId);
    const previewUrl = getTrackPreviewUrl(track);
    if (!previewUrl) {
        showToast("Для этого трека демо-прослушивание пока недоступно");
        return;
    }

    if (state.previewTrackId === trackId && previewAudio) {
        previewAudio.pause();
        previewAudio.currentTime = 0;
        state.previewTrackId = null;
        return;
    }

    if (previewAudio) {
        previewAudio.pause();
    }

    previewAudio = new Audio(previewUrl);
    previewAudio.addEventListener("ended", () => {
        state.previewTrackId = null;
        render();
    });
    previewAudio.play().catch(() => {
        state.previewTrackId = null;
        showToast("Не удалось запустить аудио-превью");
        render();
    });
    state.previewTrackId = trackId;
}

function closeTopModal() {
    if (state.detailModal) {
        state.detailModal = null;
        return;
    }
    state.editing = null;
}

function handleKeydown(event) {
    if (event.key !== "Escape" || (!state.editing && !state.detailModal)) {
        return;
    }
    closeTopModal();
    render();
}

async function handleSubmit(event) {
    const form = event.target;

    if (form.matches(".entity-form")) {
        event.preventDefault();
        await submitEntityForm(form);
        return;
    }

    if (form.matches("#album-search-form")) {
        event.preventDefault();
        await submitAlbumSearch(form);
        return;
    }

    if (form.matches("#local-filter-form")) {
        event.preventDefault();
        return;
    }

    if (form.matches("#catalog-search-form")) {
        event.preventDefault();
    }
}

async function handleInput(event) {
    const input = event.target;

    if (input.matches("#catalog-query")) {
        state.localFilters.catalog = input.value.trim();
        renderPreservingInput(input);
        return;
    }

    if (input.matches("#local-filter-input")) {
        const form = input.closest("#local-filter-form");
        const resource = form?.dataset.resource;
        if (!resource) {
            return;
        }
        state.localFilters[resource] = input.value.trim();
        resetPage(resource);
        renderPreservingInput(input);
    }
}

function renderPreservingInput(input) {
    const id = input.id;
    const cursor = input.selectionStart;
    render();
    const nextInput = document.getElementById(id);
    if (!nextInput) {
        return;
    }
    nextInput.focus();
    const nextCursor = Math.min(cursor ?? nextInput.value.length, nextInput.value.length);
    nextInput.setSelectionRange(nextCursor, nextCursor);
}

function activate(resource, { keepEditing = false } = {}) {
    if (!keepEditing) {
        state.editing = null;
    }
    state.detailModal = null;
    const nextHash = `#${resource}`;
    if (window.location.hash === nextHash) {
        state.active = resource;
        render();
    } else {
        window.location.hash = nextHash;
    }
}

async function submitEntityForm(form) {
    const resource = form.dataset.resource;
    const id = form.dataset.id ? Number(form.dataset.id) : null;
    const payload = buildPayload(resource, form, id);
    const url = id ? `${endpoints[resource]}/${id}` : endpoints[resource];
    const method = id ? "PUT" : "POST";

    await requestJson(url, {
        method,
        body: JSON.stringify(payload)
    });

    state.editing = null;
    resetPage(resource);
    await loadAll({ resetAlbumSearch: true });
    showToast("Сохранено");
}

async function submitAlbumSearch(form) {
    const data = new FormData(form);
    const params = {
        title: normalizeString(data.get("title")),
        artistName: normalizeString(data.get("artistName")),
        genreName: normalizeString(data.get("genreName")),
        yearFrom: normalizeString(data.get("yearFrom")),
        yearTo: normalizeString(data.get("yearTo"))
    };

    const query = new URLSearchParams({ page: "0", size: "100" });
    for (const [key, value] of Object.entries(params)) {
        if (value) {
            query.set(key, value);
        }
    }

    state.loading = true;

    try {
        const page = await requestJson(`/api/albums/search/jpql?${query.toString()}`);
        state.albumSearch = {
            active: true,
            items: Array.isArray(page?.content) ? page.content : [],
            total: Number(page?.totalElements || page?.content?.length || 0),
            params
        };
        resetPage("albums");
    } finally {
        state.loading = false;
        render();
    }
}

async function deleteResource(resource, id) {
    const item = getItem(resource, id);
    const name = item ? displayName(resource, item) : `#${id}`;
    if (!window.confirm(`Удалить ${name}?`)) {
        return;
    }

    await requestJson(`${endpoints[resource]}/${id}`, { method: "DELETE" });
    state.editing = null;
    resetPage(resource);
    await loadAll({ resetAlbumSearch: true });
    showToast("Удалено");
}

function buildPayload(resource, form, id) {
    const data = new FormData(form);

    switch (resource) {
        case "artists":
        case "genres":
            return {
                id,
                name: requiredString(data.get("name"), "Название")
            };
        case "albums": {
            const artistId = requiredPositiveInteger(data.get("artistId"), "Исполнитель");
            return {
                id,
                title: requiredString(data.get("title"), "Название"),
                year: requiredYear(data.get("year")),
                artistId,
                genreIds: selectedNumbers(form.elements.genreIds),
                tracks: parseTrackLines(form.elements.tracks.value, id)
            };
        }
        case "tracks":
            return {
                id,
                title: requiredString(data.get("title"), "Название"),
                durationSec: requiredPositiveInteger(data.get("durationSec"), "Длительность"),
                albumId: requiredPositiveInteger(form.elements.albumId.value, "Альбом")
            };
        case "playlists":
            return {
                id,
                name: requiredString(data.get("name"), "Название"),
                trackIds: selectedNumbers(form.elements.trackIds)
            };
        default:
            throw new Error("Неизвестный раздел");
    }
}

function parseTrackLines(value, albumId) {
    return String(value || "")
        .split(/\r?\n/)
        .map((line) => line.trim())
        .filter(Boolean)
        .map((line, index) => {
            const parts = line.split(/[|;,]/).map((part) => part.trim()).filter(Boolean);
            if (parts.length < 2) {
                throw new Error(`Трек ${index + 1}: укажите название и длительность`);
            }

            const duration = Number(parts.pop());
            const title = parts.join(" ").trim();
            if (!title || !Number.isInteger(duration) || duration < 1) {
                throw new Error(`Трек ${index + 1}: неверный формат`);
            }

            return {
                id: null,
                title,
                durationSec: duration,
                albumId: albumId || null
            };
        });
}

function selectedNumbers(select) {
    if (!select) {
        return [];
    }
    return Array.from(select.selectedOptions)
        .map((option) => Number(option.value))
        .filter((value) => Number.isInteger(value) && value > 0);
}

function requiredString(value, label) {
    const text = normalizeString(value);
    if (!text) {
        throw new Error(`${label}: заполните поле`);
    }
    return text;
}

function requiredPositiveInteger(value, label) {
    const number = Number(value);
    if (!Number.isInteger(number) || number < 1) {
        throw new Error(`${label}: выберите значение`);
    }
    return number;
}

function requiredYear(value) {
    const year = Number(value);
    if (!Number.isInteger(year) || year < 1900 || year > 2100) {
        throw new Error("Год должен быть от 1900 до 2100");
    }
    return year;
}

function getSourceItems(resource) {
    if (resource === "albums" && state.albumSearch.active) {
        return state.albumSearch.items;
    }
    return state.data[resource] || [];
}

function getPagination(resource, totalItems) {
    const totalPages = Math.max(1, Math.ceil(totalItems / PAGE_SIZE));
    const page = clampPage(state.pagination[resource] || 1, totalPages);
    state.pagination[resource] = page;
    const start = (page - 1) * PAGE_SIZE;
    return {
        page,
        totalPages,
        start,
        end: start + PAGE_SIZE
    };
}

function setPage(resource, page) {
    if (!state.pagination[resource]) {
        return;
    }
    const totalItems = getVisibleItems(resource).length;
    const totalPages = Math.max(1, Math.ceil(totalItems / PAGE_SIZE));
    state.pagination[resource] = clampPage(page, totalPages);
}

function resetPage(resource) {
    if (state.pagination[resource]) {
        state.pagination[resource] = 1;
    }
}

function clampPage(page, totalPages) {
    const nextPage = Number(page);
    if (!Number.isInteger(nextPage) || nextPage < 1) {
        return 1;
    }
    return Math.min(nextPage, totalPages);
}

function getPageButtons(page, totalPages) {
    if (totalPages <= 7) {
        return Array.from({ length: totalPages }, (_, index) => index + 1);
    }

    const pages = new Set([1, totalPages, page, page - 1, page + 1]);
    const sortedPages = Array.from(pages)
        .filter((item) => item >= 1 && item <= totalPages)
        .sort((left, right) => left - right);

    return sortedPages.flatMap((item, index) => {
        const previous = sortedPages[index - 1];
        return previous && item - previous > 1 ? ["gap", item] : [item];
    });
}

function getVisibleItems(resource) {
    const filter = normalizeForSearch(state.localFilters[resource] || "");
    const items = getSourceItems(resource);
    if (!filter) {
        return items;
    }
    return items.filter((item) => normalizeForSearch(searchText(resource, item)).includes(filter));
}

function getFilteredAlbumsForCatalog() {
    const filter = normalizeForSearch(state.localFilters.catalog || "");
    if (!filter) {
        return state.data.albums;
    }
    return state.data.albums.filter((album) => normalizeForSearch(searchText("albums", album)).includes(filter));
}

function searchText(resource, item) {
    switch (resource) {
        case "artists":
            return [item.id, item.name, getAlbumsByArtist(item.id).map((album) => album.title)].flat().join(" ");
        case "albums":
            return [
                item.id,
                item.title,
                item.year,
                getArtistName(item.artistId),
                asNumberArray(item.genreIds).map(getGenreName),
                getAlbumTracks(item).map((track) => track.title)
            ].flat().join(" ");
        case "tracks":
            return [item.id, item.title, item.durationSec, getAlbumName(item.albumId), getArtistForTrack(item)].join(" ");
        case "genres":
            return [item.id, item.name, getAlbumsByGenre(item.id).map((album) => album.title)].flat().join(" ");
        case "playlists":
            return [item.id, item.name, getTracksByIds(item.trackIds).map((track) => track.title)].flat().join(" ");
        default:
            return JSON.stringify(item);
    }
}

function getEditingItem(resource) {
    if (!state.editing || state.editing.resource !== resource) {
        return null;
    }
    if (state.editing.id === null || state.editing.id === undefined) {
        return null;
    }
    return getItem(resource, state.editing.id);
}

function getItem(resource, id) {
    const idNumber = Number(id);
    const primary = (state.data[resource] || []).find((item) => Number(item.id) === idNumber);
    if (primary) {
        return primary;
    }
    if (resource === "albums") {
        return state.albumSearch.items.find((item) => Number(item.id) === idNumber) || null;
    }
    return null;
}

function getAlbumsByArtist(artistId) {
    return state.data.albums.filter((album) => Number(album.artistId) === Number(artistId));
}

function getAlbumsByGenre(genreId) {
    const id = Number(genreId);
    return state.data.albums.filter((album) => asNumberArray(album.genreIds).includes(id));
}

function getAlbumTracks(album) {
    const embeddedTracks = Array.isArray(album?.tracks) ? album.tracks : [];
    if (embeddedTracks.length) {
        return embeddedTracks;
    }
    return state.data.tracks.filter((track) => Number(track.albumId) === Number(album?.id));
}

function getTracksByIds(trackIds) {
    const ids = new Set(asNumberArray(trackIds));
    return state.data.tracks.filter((track) => ids.has(Number(track.id)));
}

function getTrackPreviewUrl(track) {
    const titleKey = normalizeString(track?.title).toLowerCase();
    return DEMO_TRACK_PREVIEWS[titleKey] || null;
}

function getArtistName(artistId) {
    const artist = state.data.artists.find((item) => Number(item.id) === Number(artistId));
    return artist ? artist.name : `Исполнитель #${artistId}`;
}

function getAlbumName(albumId) {
    const album = state.data.albums.find((item) => Number(item.id) === Number(albumId));
    return album ? album.title : `Альбом #${albumId}`;
}

function getArtistForTrack(track) {
    const album = state.data.albums.find((item) => Number(item.id) === Number(track.albumId));
    return album ? getArtistName(album.artistId) : "";
}

function getGenreName(genreId) {
    const genre = state.data.genres.find((item) => Number(item.id) === Number(genreId));
    return genre ? genre.name : `Жанр #${genreId}`;
}

function displayName(resource, item) {
    if (!item) {
        return "";
    }
    if (resource === "tracks" || resource === "albums") {
        return item.title;
    }
    return item.name || `#${item.id}`;
}

function getItemSummaryRows(resource, item) {
    switch (resource) {
        case "artists": {
            const albums = getAlbumsByArtist(item.id);
            return [
                ["ID", item.id],
                ["Имя", item.name],
                ["Альбомы", albums.length]
            ];
        }
        case "albums": {
            const genres = asNumberArray(item.genreIds).map(getGenreName);
            const tracks = getAlbumTracks(item);
            return [
                ["ID", item.id],
                ["Название", item.title],
                ["Исполнитель", getArtistName(item.artistId)],
                ["Год", item.year || ""],
                ["Жанры", genres.length ? genres.join(", ") : "Нет жанров"],
                ["Треки", tracks.length]
            ];
        }
        case "tracks":
            return [
                ["ID", item.id],
                ["Название", item.title],
                ["Альбом", getAlbumName(item.albumId)],
                ["Исполнитель", getArtistForTrack(item)],
                ["Длительность", formatDuration(item.durationSec)]
            ];
        case "genres": {
            const albums = getAlbumsByGenre(item.id);
            return [
                ["ID", item.id],
                ["Название", item.name],
                ["Альбомы", albums.length]
            ];
        }
        case "playlists": {
            const tracks = getTracksByIds(item.trackIds);
            return [
                ["ID", item.id],
                ["Название", item.name],
                ["Треки", tracks.length]
            ];
        }
        default:
            return [["ID", item.id || ""]];
    }
}

function getDetailModalData(resource, id, listType) {
    const item = getItem(resource, id);
    if (!item) {
        return {
            title: "Список",
            items: ["Запись не найдена"]
        };
    }

    if (resource === "albums" && listType === "genres") {
        return makeDetailModalData(`Жанры: ${item.title}`, asNumberArray(item.genreIds).map(getGenreName), "Нет жанров");
    }

    if (resource === "albums" && listType === "tracks") {
        return makeDetailModalData(`Треки: ${item.title}`, getAlbumTracks(item).map(formatTrackListItem), "Треки не добавлены");
    }

    if (resource === "artists" && listType === "albums") {
        return makeDetailModalData(`Альбомы: ${item.name}`, getAlbumsByArtist(item.id).map(formatAlbumListItem), "Нет альбомов");
    }

    if (resource === "genres" && listType === "albums") {
        return makeDetailModalData(`Альбомы: ${item.name}`, getAlbumsByGenre(item.id).map(formatAlbumListItem), "Нет альбомов");
    }

    if (resource === "playlists" && listType === "tracks") {
        return makeDetailModalData(`Треки: ${item.name}`, getTracksByIds(item.trackIds).map(formatTrackListItem), "Треки не добавлены");
    }

    return {
        title: displayName(resource, item) || "Список",
        items: ["Нет данных"]
    };
}

function makeDetailModalData(title, items, emptyText) {
    const cleanItems = items.map(normalizeString).filter(Boolean);
    return {
        title,
        items: cleanItems.length ? cleanItems : [emptyText]
    };
}

function formatAlbumListItem(album) {
    return [album.title, album.year, getArtistName(album.artistId)].filter(Boolean).join(" - ");
}

function formatTrackListItem(track) {
    return [track.title, getAlbumName(track.albumId), formatDuration(track.durationSec)].filter(Boolean).join(" - ");
}

function renderGenreChips(genreIds) {
    return renderChips(asNumberArray(genreIds).map(getGenreName), "alt");
}

function renderChips(labels, variant = "") {
    const cleanLabels = labels.filter(Boolean);
    if (!cleanLabels.length) {
        return `<span class="muted">Нет данных</span>`;
    }

    return `
        <div class="chips">
            ${cleanLabels.map((label) => `<span class="chip ${variant}">${escapeHtml(label)}</span>`).join("")}
        </div>
    `;
}

function formatDuration(seconds) {
    const value = Number(seconds);
    if (!Number.isFinite(value) || value < 1) {
        return "";
    }
    const minutes = Math.floor(value / 60);
    const rest = value % 60;
    return `${minutes}:${String(rest).padStart(2, "0")}`;
}

function initials(value) {
    return String(value || "")
        .split(/\s+/)
        .filter(Boolean)
        .slice(0, 2)
        .map((part) => part[0])
        .join("")
        .toUpperCase() || "MC";
}

function hashHue(value) {
    let hash = 0;
    for (const char of String(value || "")) {
        hash = (hash * 31 + char.charCodeAt(0)) % 360;
    }
    return hash;
}

function asNumberArray(value) {
    return Array.from(value || [])
        .map((item) => Number(item))
        .filter((item) => Number.isInteger(item));
}

function normalizeString(value) {
    return String(value || "").trim();
}

function normalizeForSearch(value) {
    return String(value || "").toLocaleLowerCase("ru-RU");
}

function hasAnyData() {
    return Object.values(state.data).some((items) => items.length > 0);
}

function showToast(message) {
    const toast = document.getElementById("toast");
    toast.textContent = message;
    toast.classList.add("visible");
    window.clearTimeout(toastTimer);
    toastTimer = window.setTimeout(() => {
        toast.classList.remove("visible");
    }, 3600);
}

function handleAsyncError(error) {
    state.loading = false;
    showToast(error.message);
}

function escapeHtml(value) {
    return String(value ?? "")
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}

function escapeAttr(value) {
    return escapeHtml(value);
}
