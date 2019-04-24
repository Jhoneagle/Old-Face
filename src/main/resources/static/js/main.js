var base = contextRoot + "old-face/api";

var http = new XMLHttpRequest();

function askToBeFriend(nickname, index) {
  document.getElementById(index + 'button').style.visibility = 'hidden';
  
  var url = base + "/ask";
  var data = {
    nickname: nickname,
    accept: 1 === 2
  };
  
  http.open("POST", url);
  http.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
  http.send(JSON.stringify(data))
}

http.onreadystatechange = function() {
  if (this.readyState !== 4) {
    return
  }
  
  document.getElementById('alternative').style.visibility = 'visible';
};

var http2 = new XMLHttpRequest();

function cancel(nickname, index) {
  document.getElementById(index + 'button').style.visibility = 'hidden';
  
  var url = base + "/ask/cancel";
  var data = {
    nickname: nickname,
    accept: 1 === 2
  };
  
  http2.open("POST", url);
  http2.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
  http2.send(JSON.stringify(data))
}

http2.onreadystatechange = function() {
  if (this.readyState !== 4) {
    return
  }
  
  document.getElementById('canceled').style.visibility = 'visible';
};

var http3 = new XMLHttpRequest();

function handleRequest(nickname, accept, index) {
  document.getElementById(index + 'button').style.visibility = 'hidden';
  
  var url = base + "/request";
  var data = {
    nickname: nickname,
    accept: accept
  };
  
  http3.open("POST", url);
  http3.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
  http3.send(JSON.stringify(data))
}

http3.onreadystatechange = function() {
  if (this.readyState !== 4) {
    return
  }
  
  if (JSON.parse(this.response).accept) {
    document.getElementById('alternativeAccept').style.visibility = 'visible';
  } else {
    document.getElementById('alternativeReject').style.visibility = 'visible';
  }
};