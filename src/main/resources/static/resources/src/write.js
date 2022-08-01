import $ from "jquery";

$(()=>{
    new write();
})
export class write{
    constructor() {
        this.eventBinding();
        this.select = {
            submitBtn: '',
            inputTags: []
        }
    }
    eventBinding(){
        //업체 검색 필터
        $(document).on('keyup','#search',(e)=>{
            let keyword = e.currentTarget.value.trim();
            if(keyword.length>0) this.searchFilter(keyword);
        })
        //필터 선택
        $(document).on('click','.item',(e)=>{
            let name_input =document.getElementById("shopName");
            name_input.value= e.currentTarget.innerText;
            name_input.classList.remove('empty')
            let no_input = document.getElementById("shopNo")
            no_input.value= e.currentTarget.getElementsByClassName("name")[0].dataset.no
            no_input.classList.remove('empty')
        })
        //파일업로드 썸네일
        document.getElementById('files')
            .addEventListener('change',(e)=>{this.setThumbnail(e)})
        document.onload=()=>{
            this.select.submitBtn = document.getElementById('submit_btn');
            this.select.inputTags = document.querySelectorAll('input');
        }
        //제출전검사
        document.querySelectorAll('.required').forEach((input)=>{
                input.classList.add('empty');
                input.addEventListener('change',(e)=>{
                    if(e.currentTarget.value.trim().length>0){
                        e.currentTarget.classList.remove('empty')
                    }else{
                        e.currentTarget.classList.add('empty')
                    }
                    document.getElementById('submit_btn').disabled
                        =document.querySelectorAll('.empty').length>0
                })
            }
        )
    }
    searchFilter(keyword){
        let items = document.querySelectorAll(".item");
        items.forEach((item, index, list)=>{
            let name = item.getElementsByClassName("name")[0].innerText;
            if(name.indexOf(keyword)>-1){
                item.classList.remove('hidden');
            }else{
                item.classList.add('hidden')
            }
        })
    }
    checkImage(img){
        let arr = ['png','jpg','jpeg'];
        let extend = img.substring(img.lastIndexOf('.')+1);
        return arr.indexOf(extend.toLowerCase())>-1;
    }
    setThumbnail(e) {
        document.querySelectorAll('img')
            .forEach((img)=>{img.parentNode.removeChild(img)});
        for(let file of e.target.files){
            console.log("test-",file)
            if(this.checkImage(file.name)){
                //
                let reader = new FileReader();
                reader.onload = function (e) {
                    let img = document.createElement("img");
                    img.setAttribute("src", e.target.result);
                    img.setAttribute("style","max-height:100px");
                    document.querySelector(".thumb_container").appendChild(img);
                };
                reader.readAsDataURL(file);
            }
        }
    }
}