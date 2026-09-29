document.addEventListener("DOMContentLoaded", () => {

    const toggleBtn = document.getElementById("chatToggleBtn");
    const chatContainer = document.getElementById("chatContainer");
    const closeBtn = document.getElementById("closeChatBtn");

    const sendBtn = document.getElementById("sendBtn");
    const input = document.getElementById("userMessage");

    const chatMessages = document.getElementById("chatMessages");
    const typingIndicator = document.getElementById("typingIndicator");

    /*==============================
            Open Chat
    ==============================*/

    toggleBtn.addEventListener("click", () => {

        chatContainer.style.display = "flex";

        toggleBtn.style.display = "none";

        input.focus();

    });

    /*==============================
            Close Chat
    ==============================*/

    closeBtn.addEventListener("click", () => {

        chatContainer.style.display = "none";

        toggleBtn.style.display = "flex";

    });

    /*==============================
            Enter Key
    ==============================*/

    input.addEventListener("keypress", function (e) {

        if (e.key === "Enter") {

            sendMessage();

        }

    });

    sendBtn.addEventListener("click", sendMessage);

    /*==============================
            Send Message
    ==============================*/

    async function sendMessage() {

        const message = input.value.trim();

        if (message === "")
            return;

        addUserMessage(message);

        input.value = "";

        showTyping();

        try {

            const response = await fetch("/api/ai/chat", {

                method: "POST",

                headers: {

                    "Content-Type": "application/json"

                },

                body: JSON.stringify({

                    message: message

                })

            });

            const data = await response.json();

            hideTyping();

            addAiMessage(data.reply);

        } catch (error) {

            hideTyping();

            addAiMessage("Sorry! Unable to connect with Aura AI.");

            console.error(error);

        }

    }

    /*==============================
            User Message
    ==============================*/

    function addUserMessage(text) {

        const wrapper = document.createElement("div");

        wrapper.className = "user-message";

        wrapper.innerHTML = `

            <div class="message">

                ${text}

            </div>

        `;

        chatMessages.appendChild(wrapper);

        scrollBottom();

    }

    /*==============================
            AI Message
    ==============================*/

	function formatMessage(text){

	    return text
	        .replace(/\n/g,"<br>")
	        .replace(/\*\*(.*?)\*\*/g,"<strong>$1</strong>")
	        .replace(/\*(.*?)\*/g,"<em>$1</em>");

	}
	
	function addAiMessage(text) {

	    const wrapper = document.createElement("div");

	    wrapper.className = "ai-message";

	    wrapper.innerHTML = `

	        <div class="avatar">

	            <i class="fa-solid fa-meteor"></i>

	        </div>

	        <div class="message">

	            ${formatMessage(text)}

	        </div>

	    `;

	    chatMessages.appendChild(wrapper);


	    scrollBottom();

	}
    /*==============================
            Typing
    ==============================*/

    function showTyping() {

        typingIndicator.style.display = "flex";

        scrollBottom();

    }

    function hideTyping() {

        typingIndicator.style.display = "none";

    }

    /*==============================
            Scroll
    ==============================*/

    function scrollBottom() {

        chatMessages.scrollTop = chatMessages.scrollHeight;

    }

}); 
/*=========================================
            PART 2
=========================================*/

/* ==============================
        Clear Chat
==============================*/

window.clearAuraChat=function(){

    localStorage.removeItem(STORAGE_KEY);

    chatMessages.innerHTML=`

        <div class="ai-message">

            <div class="avatar">

                <i class="fa-solid fa-meteor"></i>

            </div>

            <div class="message">

                <b>Hello 👋</b><br><br>

                I'm <b>Aura AI</b>, your intelligent shopping assistant.

            </div>

        </div>

    `;

}

/* ==============================
        Type Writer Effect
==============================*/

function typeWriterEffect(text){

    const wrapper=document.createElement("div");

    wrapper.className="ai-message";

    wrapper.innerHTML=`

        <div class="avatar">

            <i class="fa-solid fa-meteor"></i>

        </div>

        <div class="message"></div>

    `;

    chatMessages.appendChild(wrapper);

    const box=wrapper.querySelector(".message");

    let i=0;

    const speed=12;

    function typing(){

        if(i<text.length){

            box.innerHTML+=text.charAt(i);

            i++;

            scrollBottom();

            setTimeout(typing,speed);

        }
    }

    typing();

}

/* ==============================
        Better User Message
==============================*/

const oldAddUser=addUserMessage;

addUserMessage=function(msg){

    oldAddUser(msg);


}

/* ==============================
        Auto Focus
==============================*/

toggleBtn.addEventListener("click",()=>{

    setTimeout(()=>{

        input.focus();

    },300);

});

/* ==============================
        Escape Key
==============================*/

document.addEventListener("keydown",(e)=>{

    if(e.key==="Escape"){

        chatContainer.style.display="none";

        toggleBtn.style.display="flex";

    }

});

/* ==============================
        Disable Button
==============================*/

async function disableButton(){

    sendBtn.disabled=true;

    sendBtn.style.opacity=".6";

}

async function enableButton(){

    sendBtn.disabled=false;

    sendBtn.style.opacity="1";

}

/* ==============================
        Replace sendMessage
==============================*/

const originalSend=sendMessage;

sendMessage=async function(){

    const msg=input.value.trim();

    if(msg==="") return;

    await disableButton();

    await originalSend();

    enableButton();

}

/* ==============================
        Copy AI Message
==============================*/

chatMessages.addEventListener("dblclick",(e)=>{

    const msg=e.target.closest(".message");

    if(!msg) return;

    navigator.clipboard.writeText(msg.innerText);

});

/* ==============================
        Welcome Animation
==============================*/

setTimeout(()=>{

    toggleBtn.classList.add("welcome");

},1200);