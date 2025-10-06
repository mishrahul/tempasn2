export function isAuthenticated(): boolean {
    const token = sessionStorage.getItem('pxp_token');
    return !!token;
}

export function hasSelectedOEM(): boolean {
    const isOEM = sessionStorage.getItem('selectedOEM');
    return !!isOEM;
}