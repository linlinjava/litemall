const IMAGE_EXT = ['jpeg', 'jpg', 'gif', 'png', 'svg'];
export function isImageUrl(url) {
    return IMAGE_EXT.some(ext => url.indexOf(`.${ext}`) !== -1);
}
export function isImageFile(item) {
    if (item.type) {
        return item.type.indexOf('image') === 0;
    }
    if (item.path) {
        return isImageUrl(item.path);
    }
    if (item.url) {
        return isImageUrl(item.url);
    }
    return false;
}
