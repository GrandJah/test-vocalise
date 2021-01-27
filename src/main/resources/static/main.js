const changeStatusWorks = isWorks => {
    document.getElementById("in").hidden = isWorks;
    document.getElementById("out").hidden = !isWorks;
}

const authModeOn = isAuth => {
    document.getElementById("action").hidden = !isAuth;
    document.getElementById("login").hidden = isAuth;
}

const toggleText = element => {
    const text = element.getAttribute("_toggle");
    if (text !== undefined) {
        element.setAttribute("_toggle", element.innerText);
        element.innerText = text;
    }
}

let actionForm = false;

let registeredForm = false;

const registration = () => {
    registeredForm = !registeredForm;
    document.getElementById("loginButton").hidden = registeredForm;
    toggleText(document.getElementById("toggleForm"));
    document.getElementById("registrationButton").hidden = !registeredForm;
    document.getElementById("status_registration").innerText = "";
}

const ajax = (url, data, callback) => {
    fetch(`/${url}`, {
        method: 'post',
        headers: {
            "Content-type": "application/json; charset=UTF-8;"
        },
        body: JSON.stringify(data)
    })
        .then(response => {
            const auth = response.status !== 403
            actionForm &= auth
            authModeOn(actionForm)
            if (auth) {
                response.json().then(json => {
                    if (json.success === true && json.message !== undefined) {
                        if (json.message === 'in' || json.message === 'out') {
                            changeStatusWorks(json.message === 'in');
                        }
                    }
                    if (callback !== undefined) {
                        callback(json);
                    }
                })
            }
        })
}

let timer;

const getStatus = () => {
    ajax("status", {}, () => {
        clearTimeout(timer);
        timer = setTimeout(getStatus, 5000);
    })
}

function setTimeWorkString() {
    console.log("скачать отчет")
}


const act = (action, fun) => {
    ajax(action, {}, fun);
}

const userFormAjax = (action, fun) => {
    const form = document.getElementById("loginForm");
    const user = {login: form.elements.login.value, password: form.elements.password.value};
    ajax(action, user, fun);
}

const logIn = () => {
    userFormAjax("log-in", function (status) {
        document.getElementById("status_registration").innerText = !status.success ? status.message : "";
        actionForm = status.success;
        getStatus();
    });
}

const logOut = () => {
    act('log-out', () => {
        actionForm = false;
        getStatus();
    });
}

const registrationAccount = () => {
    userFormAjax("registration", status => {
        document.getElementById("status_registration").innerText = status.message;
    });
}

const validate = {};

const changeTagValid = (el, tagTrue, tagFalse) => {
    const flag = el.validity.valid;
    validate[el.name] = flag;
    el.classList.remove(flag ? tagFalse : tagTrue);
    el.classList.add(flag ? tagTrue : tagFalse);
}

const checkValid = (...tags) => {
    let flag = true;
    tags.forEach(el => flag &= validate[el])
    return flag;
}

const checkButton = () => {
    const flag = checkValid("login", "password");
    document.getElementById("loginButton").disabled = !flag;
    document.getElementById("registrationButton").disabled = !flag;
}

(() => {
    getStatus();
    document.querySelectorAll(".need-valid").forEach(node => {
        node.onchange = () => {
            changeTagValid(node, 'is-valid', 'is-invalid');
            checkButton();
        };
    })
})()
