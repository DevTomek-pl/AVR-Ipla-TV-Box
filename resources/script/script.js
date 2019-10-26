// https://material.io/tools/icons/?style=baseline
window.isFullscreen = false;

window.toggleFullscreen = function () {
    if (!isFullscreen) {
        var style = document.createElement('style');
        style.innerHTML = '.cpp2-player-container video {width: 100% !important; height: 100% !important; position: fixed; z-index: 1000 !important;} .header-wrapper {display: none;} html {overflow: hidden !important;}';
        var ref = document.querySelector('script');
        ref.parentNode.insertBefore(style, ref);
        isFullscreen = true;
    } else {
        document.getElementsByTagName('style')[1].remove();
        isFullscreen = false;
    }
};

window.snackbarTimer = undefined;

window.showSnackbar = function (text, topPosition) {
    var snackbar = document.getElementById("snackbar");
    snackbar.className = "show";
    snackbar.style.top = (topPosition !== undefined) ? topPosition + 'px' : "";
    snackbar.innerHTML = text;
    clearTimeout(snackbarTimer);
    snackbarTimer = setTimeout(() => {
        snackbar.style.top = "";
        snackbar.className = snackbar.className.replace("show", "hide");
    }, 5000)
};

(function () {
    // style
    var style = document.createElement('style');
    style.innerHTML = '#snackbar { margin: 0; position: absolute; z-index: 9999; left: 50%; -ms-transform: translate(-50%, -50%); transform: translate(-50%, -50%); min-width: 250px; background-color: #232323; color: #007bff; text-align: center; border-radius: 5px; padding: 16px; font-size: ${HEADER_BIG_FONT_SIZE}pt; -webkit-transition: all 600ms; transition: all 600ms; white-space: nowrap; } #snackbar.show { top: 90px; visibility: visible; opacity: ${OPACITY}; } #snackbar.hide { top: -90px; visibility: hidden; opacity: 0; }' +
    '#snackbar h1 { margin: 0px; font-weight: bold; font-size: ${HEADER_BIG_FONT_SIZE}pt; } #snackbar h2 { margin: 0px; font-size:${HEADER_SMALL_FONT_SIZE}pt; } #snackbar hr { margin: 14px; }' +
    "@font-face { font-family: 'Material Icons'; font-style: normal; font-weight: 400; src: url(https://fonts.gstatic.com/s/materialicons/v47/flUhRq6tzZclQEJ-Vdg-IuiaDsNc.woff2) format('woff2'); } .material-icons { margin: 0px; font-family: 'Material Icons'; font-weight: normal; font-style: normal; font-size: 62pt; line-height: 1; letter-spacing: normal; text-transform: none; display: inline-block; white-space: nowrap; word-wrap: normal; direction: ltr; -webkit-font-feature-settings: 'liga'; -webkit-font-smoothing: antialiased; }";
    var ref = document.querySelector('script');
    ref.parentNode.insertBefore(style, ref);
    // html
    var snackbar = document.createElement('div');
    snackbar.id = 'snackbar';
    snackbar.class = 'hide';
    document.body.appendChild(snackbar);
})();

window.displaySnackbarMessageWithCurrentProgram = function (channel, dateRange, progress, title) {
    showSnackbar('<h2>' + channel + '</h2><h2>' + dateRange + ' (' + progress + '%)</h2><hr><h1>' + title + '</h1>', 160);
};

window.setBrightness = function (value) {
    let body = document.querySelector('body');
    body.style.backgroundColor = '#000000';
    body.style.filter = 'brightness(' + value + '%)'
};

window.changePlayerTime = function (difInSeconds) {
    var icon = 'play_circle_outline';
    if (difInSeconds > 0){
        icon = 'forward_10'
    } else if (difInSeconds < 0){
        icon = 'replay_10';
    }
    var currentTime = document.querySelector('video').currentTime;
    document.querySelector('video').currentTime = currentTime + difInSeconds;
    var date = new Date((currentTime + difInSeconds + 3600) * 1000);
    var dateFormatted = date.getHours() + ':' + format(date.getMinutes()) + ':' + format(date.getSeconds());
    showSnackbar('<h1><i class="material-icons">' + icon + '</i></h1><h1>' + dateFormatted + '</h1>', 140);
};

window.displayCurrentTime = function (icon) {
    var currentTime = document.querySelector('video').currentTime;
    var date = new Date((currentTime + 3600) * 1000);
    var dateFormatted = date.getHours() + ':' + format(date.getMinutes()) + ':' + format(date.getSeconds());
    showSnackbar('<h1><i class="material-icons">' + icon + '</i></h1><h1>' + dateFormatted + '</h1>', 140);
};

window.format = function (date) {
    return (date > 9) ? date : '0' + date;
};

window.displayIcon = function (icon) {
    showSnackbar('<h1><i class="material-icons">' + icon + '</i><h1/>');
};

window.play = function () {
    document.querySelector('video').play();
    displayCurrentTime('play_circle_outline');
};

window.pause = function () {
    document.querySelector('video').pause();
    displayCurrentTime('pause_circle_outline');
};

window.goToNow = function () {
    document.querySelector('.cpp2-go-to-now-button').click();
    displayCurrentTime('settings_backup_restore');
};

window.changeResolution = function () {
    var index = -1;
    var resolutionElementList = document.querySelectorAll('.cpp2-menu-content-row div.cpp2-button.button--3M_f2');
    for (let e of resolutionElementList) {
        index++;
        if (e.classList.contains('cpp2-active')) {
            nextIndex = ((index + 1) < resolutionElementList.length) ? index + 1 : 0;
            resolutionElementList[nextIndex].click();
            showSnackbar('<h1>Resolution: ' + (resolutionElementList[nextIndex].textContent) + '</h1>');
            break;
        }
    }
}

setBrightness(${BRIGHTNESS});
toggleFullscreen();