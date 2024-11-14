$(document).ready(() => {
    console.log('페이지 로딩 완료');
    checkLoginStatus();
    setupEventListeners();
});

// 로그인 상태 확인 및 UI 업데이트
function checkLoginStatus() {
    const token = localStorage.getItem("accessToken");
    if (!token) {
        updateUIForLoggedOutState();
        return;
    }

    // 토큰 유효성 검사
    if (isTokenExpired(token)) {
        handleTokenExpiration();
        return;
    }

    getUserInfo(token)
        .then(showUserInfo)
        .catch(error => {
            console.error('Error getting user info:', error);
            handleTokenExpiration();
        });
}

function isTokenExpired(token) {
    const payloadBase64 = token.split('.')[1];
    const decodedJson = atob(payloadBase64);
    const decoded = JSON.parse(decodedJson);
    const exp = decoded.exp;
    return (Date.now() >= exp * 1000);
}

// 비로그인시 보여줄 UI
function updateUIForLoggedOutState() {
    $('#auth-buttons').show();
    $('#member-info').empty();
    $('#logout-btn').hide();
    $('#admin-content').hide();
}

// 로그인시 보여줄 UI
function showUserInfo(userInfo) {
    console.log('Received user info:', userInfo); // 디버깅용
    $('#auth-buttons').hide();
    $('#member-info').html(`<p>로그인 사용자: ${userInfo.userName || 'Unknown'}</p>`);
    $('#logout-btn').show();

    if (userInfo.role === 'ROLE_ADMIN') {
        $('#admin-content').show();
    } else {
        $('#admin-content').hide();
    }
}

// 이벤트 리스너 설정
function setupEventListeners() {
    // $('#logout-btn').click(handleLogout);
    $('#login-btn').click(() => navigateTo('/member/login'));
    $('#signup-btn').click(() => navigateTo('/member/join'));
    $('#restricted-btn').click(handleRestrictedAccess);
}

// 로그아웃
function handleLogout() {
    $.ajax({
        type: 'POST',
        url: '/logout',
        contentType: 'application/json; charset=utf-8',
        success: (response) => {
            alert(response.message);
            localStorage.removeItem('accessToken');
            navigateTo(response.url);
        },
        error: (error) => {
            console.log('logout 오류 발생 :: ', error);
            alert('로그아웃 중 오류가 발생했습니다!');
        }
    });
}

function navigateTo(url) {
    window.location.href = url;
}

// 토큰이 만료되었을 시 refresh-token 을 이용해 재발급
function handleTokenExpiration() {
    const refreshToken = localStorage.getItem("refreshToken");
    if (!refreshToken) {
        alert('로그인이 필요합니다. 다시 로그인해주세요.');
        navigateTo('/member/login');
        return;
    }

    $.ajax({
        type: 'POST',
        url: '/refresh-token',
        contentType: 'application/json; charset=utf-8',
        dataType: 'json',
        data: JSON.stringify({ refreshToken: refreshToken }),
        success: (response) => {
            localStorage.setItem('accessToken', response.accessToken);
            if (response.refreshToken) {
                localStorage.setItem('refreshToken', response.refreshToken);
            }
            checkLoginStatus();
        },
        error: (xhr) => {
            console.error('Token refresh error:', xhr);
            alert('세션이 만료되었습니다. 다시 로그인해주세요.');
            localStorage.removeItem('accessToken');
            localStorage.removeItem('refreshToken');
            navigateTo('/member/login');
        }
    });
}

// 토큰 기반으로 한 사용자 정보 조회
function getUserInfo(token) {
    return new Promise((resolve, reject) => {
        $.ajax({
            type: 'GET',
            url: '/user/info',
            headers: { 'Authorization': 'Bearer ' + token },
            success: (response) => {
                console.log('User info response:', response); // 디버깅용
                resolve(response);
            },
            error: (xhr) => {
                console.error('Error fetching user info:', xhr); // 디버깅용
                if (xhr.status === 401) {
                    handleTokenExpiration();
                } else {
                    reject(xhr);
                }
            }
        });
    });
}

// Role 이 ADMIN 일때 접근할 수 있는 콘텐츠
function handleRestrictedAccess() {
    const token = localStorage.getItem("accessToken");
    if (token) {
        getUserInfo(token)
            .then(userInfo => {
                if (userInfo.role === 'ROLE_ADMIN') {
                    $('#admin-content').show();
                } else {
                    alert('관리자만 접근할 수 있는 콘텐츠입니다.');
                    $('#admin-content').hide();
                }
            })
            .catch(handleTokenExpiration);
    } else {
        alert('로그인이 필요합니다.');
        navigateTo('/member/login');
    }
}