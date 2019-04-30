var base = contextRoot + "old-face/api";

var http = new XMLHttpRequest();

function askToBeFriend(nickname, index) {
  document.getElementById(index + 'button').style.display = 'none';
  
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
  
  document.getElementById('alternative').style.display = 'inline';
};

var http2 = new XMLHttpRequest();

function cancel(nickname, index) {
  document.getElementById(index + 'button').style.display = 'none';
  
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
  
  document.getElementById('canceled').style.display = 'inline';
};

var http3 = new XMLHttpRequest();

function handleRequest(nickname, accept, index) {
  document.getElementById(index + 'button').style.display = 'none';
  
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
    document.getElementById('alternativeAccept').style.display = 'inline';
  } else {
    document.getElementById('alternativeReject').style.display = 'inline';
  }
};

var http4 = new XMLHttpRequest();

function like(id, index) {
  document.getElementById('addLike' + index).style.display = 'none';
  document.getElementById('likes' + index).textContent = parseInt(document.getElementById('likes' + index).innerHTML) + 1;
  
  var url = base + "/post/like";
  var data = {
    id: id,
    content: ''
  };
  
  http4.open("POST", url);
  http4.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
  http4.send(JSON.stringify(data))
}

http4.onreadystatechange = function() {
  if (this.readyState !== 4) {
    return
  }
  
  document.getElementById('removeLike' + index).style.display = 'inline';
};

var http5 = new XMLHttpRequest();

function unlike(id, index) {
  document.getElementById('removeLike' + index).style.display = 'none';
  document.getElementById('likes' + index).textContent = parseInt(document.getElementById('likes' + index).innerHTML) - 1;
  
  var url = base + "/post/like";
  var data = {
    id: id,
    content: ''
  };
  
  http5.open("POST", url);
  http5.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
  http5.send(JSON.stringify(data))
}

http5.onreadystatechange = function() {
  if (this.readyState !== 4) {
    return
  }
  
  document.getElementById('addLike' + index).style.display = 'inline';
};

var http6 = new XMLHttpRequest();

function getComments(id, index) {
  document.getElementById('getComments' + index).style.display = 'none';
  
  var url = base + "/post/get";
  var data = {
    id: id,
    content: ''
  };
  
  http6.open("POST", url);
  http6.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
  http6.send(JSON.stringify(data))
}

http6.onreadystatechange = function() {
  if (this.readyState !== 4) {
    return
  }
  
  document.getElementById('showComments').style.display = 'inline';
  
  var data = JSON.parse(this.response);
  alert(data)
};