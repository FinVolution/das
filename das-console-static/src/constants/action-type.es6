export const actionType = {
    BASE_URL: window.location.hostname == 'location' || window.location.hostname == '127.0.0.1' ? 'http://' + window.location.hostname + ':8080': window.location.origin,
    LOGIN_ACCOUNT:'LocalStorageData_LOGIN_ACCOUNT'
}