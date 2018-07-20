$(function(){

    function tip(msg){
        var $tipElement = $('<div class="tip-wrap"><div class="tip">'+ msg +'</div></div>');
        $('body').append($tipElement);
        var $tip = $('.tip-wrap .tip');
        var w = $tip.width();
        var h = $tip.height();
        $tip.css({
            marginLeft: '-' + (w/2) + 'px',
            marginTop: '-' + (h/2) + 'px'
        });
        setTimeout(function(){
            $('.tip-wrap').remove();
        },1500);
    }

    $(document).on('click','#login',function(){
        var username = $('.left input[name="username"]').val();
        var password = $('.left input[name="password"]').val();

        var url = '/login';
        var args = {"username": username,"password": password};
        $.ajax({
            type: 'POST',
            url: url,
            data: JSON.stringify(args),
            contentType: 'application/json',
            dataType: 'json',
            success: function(data){
                if(data.code == 0){
                    tip(data.msg);
                    setTimeout(function () {
                        location.href = data.data;
                    },1500)
                }else{
                    tip(data.msg);
                }
            },
            error: function(data){
                tip(data);
                console.log(data);
            }
        });
    });

    $(document).on('click','#register',function(){
        var username = $('.right input[name="username"]').val();
        var password = $('.right input[name="password"]').val();
        var show = $('.right input[name="show"]').val();

        var url = '/register';
        var args = {"username": username,"password": password, "show": show};
        $.ajax({
            type: 'POST',
            url: url,
            data: JSON.stringify(args),
            contentType: 'application/json',
            dataType: 'json',
            success: function(data){
                if(data.code == 0){
                    tip(data.msg);
                    setTimeout(function () {
                        location.href = '/';
                    },1500)
                }else{
                    tip(data.msg);
                }
            },
            error: function(data){
                tip(data);
                console.log(data);
            }
        });
    })
});