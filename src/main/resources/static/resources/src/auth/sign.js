import axios from "axios";
import $ from "jquery";

const LOGIN_URL="/auth2/login"
const SIGNUP_URL="/auth/signup"

$(()=>{
    new sign();
})

export class sign{
    constructor() {
        this.eventBinding()
        const inChkPw=false;
    }
    eventBinding(){
        //pw확인
        document.getElementById('pw2').addEventListener('keyup',(e)=>{
            this.isChkPw = document.getElementById('pw').value===e.target.value
        })
        //양식채움
        document.getElementById('submit').addEventListener('click',(e)=>{
            let data={};
            let form = document.getElementById('signup_form');
            let nameList = Array.from(form).filter(i=>i.tagName==='INPUT').map(input=>{return input.name})
            nameList.forEach((name)=>{ data[name] = form[name].value.trim() })
            let isFilled = Object.values(data).every((v=>{return v!==''}))
            console.log(data, "/n filled all: ", isFilled);

            if(isFilled && this.isChkPw){this.signUp(data)}
        })
        //로그인
        document.getElementById('login_btn').addEventListener('click',(e)=>{
            let data={};
            data.userId=document.getElementById('login_form').userId.value.trim()
            data.pw=document.getElementById('login_form').pw.value.trim()
            let isFilled = Object.values(data).every((v=>{return v!==''}))
            if(isFilled){this.login(new FormData(data))}
        })

    }
    signUp(data){
        axios.post(SIGNUP_URL,  JSON.stringify(data), {headers: {"Content-Type": `application/json`,},})
            .then((res) => {
                console.log(res);
            });
    }
    login(formData){
        axios.post(LOGIN_URL, formData, {headers:{"Content-Type":"multipart/form-data"}})
            .then((res) => {
                console.log(res)
                document.localStorage.setItem("access_token",res.headers.access_token)
                window.location.href("/")
            }).catch( e =>{
                console.log(e)
            });
    }


}