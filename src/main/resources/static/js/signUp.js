$(document).ready(() => {
    $('#signup').click(() => {
        let userId = $('#user_id').val();
        let password = $('#password').val();
        let userName = $('#user_name').val();

        let formData = {
            userId: userId,
            password: password,
            userName: userName
            // role은 서버 측에서 처리하므로 여기서는 보내지 않습니다.
        };

        $.ajax({
            type: 'POST',
            url: '/join',
            data: JSON.stringify(formData),
            contentType: 'application/json; charset=utf-8',
            dataType: 'json',
            success: function(response) {
                alert('회원가입이 성공했습니다.\n로그인해주세요.');
                window.location.href = response.url;
            },
            error: function(error) {
                console.error('오류 발생:', error);
                alert('회원가입 중 오류가 발생했습니다.');
            }
        });
    });
});