var base = contextRoot + "old-face/api";

var http = new XMLHttpRequest();

function askToBeFriend(nickname, index) {
  document.getElementById('ask' + nickname).style.display = 'none';
  document.getElementById('cancel' + nickname).style.display = 'inline';
  
  var url = base + "/ask";
  var data = {
    nickname: nickname,
    accept: 1 === 2
  };
  
  http.open("POST", url);
  http.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
  http.send(JSON.stringify(data))
}

function cancel(nickname, index) {
  document.getElementById('cancel' + nickname).style.display = 'none';
  document.getElementById('ask' + nickname).style.display = 'inline';
  
  var url = base + "/ask/cancel";
  var data = {
    nickname: nickname,
    accept: 1 === 2
  };
  
  http.open("POST", url);
  http.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
  http.send(JSON.stringify(data))
}

function like(id, index) {
  document.getElementById('addLike' + index).style.display = 'none';
  document.getElementById('likes' + index).textContent = parseInt(document.getElementById('likes' + index).innerHTML) + 1;
  document.getElementById('removeLike' + index).style.display = 'inline';
  
  var url = base + "/post/like";
  var data = {
    id: id,
    content: ''
  };
  
  http.open("POST", url);
  http.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
  http.send(JSON.stringify(data))
}

function unlike(id, index) {
  document.getElementById('removeLike' + index).style.display = 'none';
  document.getElementById('likes' + index).textContent = parseInt(document.getElementById('likes' + index).innerHTML) - 1;
  document.getElementById('addLike' + index).style.display = 'inline';
  
  var url = base + "/post/like";
  var data = {
    id: id,
    content: ''
  };
  
  http.open("POST", url);
  http.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
  http.send(JSON.stringify(data))
}

function likeImage(id) {
  document.getElementById('addLike').style.display = 'none';
  document.getElementById('likes').textContent = parseInt(document.getElementById('likes').innerHTML) + 1;
  document.getElementById('removeLike').style.display = 'inline';
  
  var url = base + "/image/like";
  var data = {
    id: id,
    content: ''
  };
  
  http.open("POST", url);
  http.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
  http.send(JSON.stringify(data))
}

function unlikeImage(id) {
  document.getElementById('removeLike').style.display = 'none';
  document.getElementById('likes').textContent = parseInt(document.getElementById('likes').innerHTML) - 1;
  document.getElementById('addLike').style.display = 'inline';
  
  var url = base + "/image/like";
  var data = {
    id: id,
    content: ''
  };
  
  http.open("POST", url);
  http.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
  http.send(JSON.stringify(data))
}

http.onreadystatechange = function() {
  if (this.readyState !== 4) {
    return
  }
};

var http2 = new XMLHttpRequest();

function handleRequest(nickname, accept, index) {
  document.getElementById(index + 'button').style.display = 'none';
  
  var url = base + "/request";
  var data = {
    nickname: nickname,
    accept: accept
  };
  
  http2.open("POST", url);
  http2.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
  http2.send(JSON.stringify(data))
}

http2.onreadystatechange = function() {
  if (this.readyState !== 4) {
    return
  }
  
  if (JSON.parse(this.response).accept) {
    document.getElementById('alternativeAccept').style.display = 'inline';
  } else {
    document.getElementById('alternativeReject').style.display = 'inline';
  }
};

var http3 = new XMLHttpRequest();
var select;

function getComments(id, index) {
  document.getElementById('getComments' + index).style.display = 'none';
  document.getElementById('commentsBlog' + index).style.display = 'inline';
  
  var url = base + "/post/get";
  select = index;
  var data = {
    id: id,
    content: ''
  };
  
  http3.open("POST", url);
  http3.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
  http3.send(JSON.stringify(data))
}

function createComments(id, index) {
  document.getElementById('getComments' + index).style.display = 'none';
  
  var url = base + "/post";
  select = index;
  var data = {
    id: id,
    content: document.getElementById("comment").value
  };
  
  http3.open("POST", url);
  http3.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
  http3.send(JSON.stringify(data))
}

http3.onreadystatechange = function() {
  if (this.readyState !== 4) {
    return
  }
  
  var response = JSON.parse(this.responseText);
  var taskList = "";
  
  for (var i = 0; i < response.length; i++) {
    var string = '<h4>' + response[i].creator + ' <small>' + response[i].timestamp + '</small></h4>' +
      '<p>' + response[i].content + '</p>';
    
    taskList += string;
  }
  
  document.getElementById("showComments" + select).innerHTML = taskList;
  document.getElementById("comment").value = ""
};

var http4 = new XMLHttpRequest();

function getCommentsOfImage(id) {
  document.getElementById('getComments').style.display = 'none';
  document.getElementById('commentsImage').style.display = 'inline';
  
  var url = base + "/image/get";
  var data = {
    id: id,
    content: ''
  };
  
  http4.open("POST", url);
  http4.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
  http4.send(JSON.stringify(data))
}

function createCommentForImage(id) {
  document.getElementById('getComments').style.display = 'none';
  
  var url = base + "/image";
  var data = {
    id: id,
    content: document.getElementById("comment").value
  };
  
  http4.open("POST", url);
  http4.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
  http4.send(JSON.stringify(data))
}

http4.onreadystatechange = function() {
  if (this.readyState !== 4) {
    return
  }
  
  var response = JSON.parse(this.responseText);
  var taskList = "";
  
  for (var i = 0; i < response.length; i++) {
    var string = '<h4>' + response[i].creator + ' <small>' + response[i].timestamp + '</small></h4>' +
      '<p>' + response[i].content + '</p>';
    
    taskList += string;
  }
  
  document.getElementById("showComments").innerHTML = taskList;
  document.getElementById("comment").value = ""
};