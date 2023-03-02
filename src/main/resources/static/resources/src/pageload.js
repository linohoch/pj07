"use strict";

import tmplt from "../../../templates/board/addShop.html";
import write_tmplt from "../../../templates/board/write.html";
import {CancelLike, likeArticle, likeComment} from "./api"

const axios = require('axios');
const $ = require("jquery");
const cors = require('cors');
let idx = null;
let latlng = null;
const COMMENT_URL='/board/comment/ins';

$(()=>{
    new content_page();
})

export class content_page {

    constructor() {
        // this.page_load();
        // this.page_write();
        // this.page_edit();
        // this.page_delete();
        //this.page_edit_submit();
        // this.file_upload();
        // this.nav_down();
        this.pic_view();
        this.eventBinding();

        this.select = {
            focusShop: '',
            submitBtn: '',
            inputTags: []
        }
    }
    eventBinding(){
        window.onload = ()=> {
    //버튼

            $(document).on('click', '.write_btn', () => {
                $('.listWrapper').html(write_tmplt);
            })
            $(document).on('click', '.addShop_btn', () => {
                // const template=require("../../../templates/board/addShop.html");
                // let wrapper=document.getElementsByClassName('.listWrapper')
                $('.listWrapper').html(tmplt);
            })
            document.querySelector('.logout_btn').addEventListener('click', () => {
                // document.cookie = "max-age:0;"
                // axios.post('/logout')
            })

            //shopCard를 article로 임시로 쓰는 중
            //게시글클릭
            document.querySelectorAll('.shopCard').forEach((el) => {
                el.addEventListener('click', (ev) => {
                    let articleNo = ev.currentTarget.dataset.key;
                    let url = `/board/article/${articleNo}`;
                    window.location.href = url;
                })
            })
            //게시글좋아요
            const $likeBtn = document.querySelector('.like_btn')
            if ($likeBtn !== undefined && $likeBtn !== null){
                $likeBtn.addEventListener('click', (e) => {
                    let likeYn = e.currentTarget.dataset.liked;
                    let articleNo = e.currentTarget.dataset.articleNo;
                    if (likeYn === 'n') {
                        likeArticle(articleNo).then(r => {
                                if (r.status === 201) {
                                    $likeBtn.dataset.liked = 'y'
                                    $likeBtn.innerText = 'like cancel'
                                }
                            }
                        )
                    } else {
                        CancelLike(articleNo).then(r => {
                            if (r.status === 201) {
                                $likeBtn.dataset.liked = 'n'
                                $likeBtn.innerText = 'like'
                            }
                        })
                    }
                })
            }
            //댓글좋아요
            const likeBtnC = document.querySelector('.comment_like_btn')
            if (likeBtnC !== undefined && likeBtnC !== null){
                likeBtnC.addEventListener('click', async (e) => {
                    let likeYn = e.currentTarget.dataset.likeyn;
                    let articleNo = e.currentTarget.dataset.articleNo;
                    let commentNo = e.currentTarget.dataset.commentNo;
                    const result = await likeComment(articleNo, commentNo, likeYn);
                    console.log('likebtn -> ',result.status, result.data)
                    if (result.data) {
                        likeBtnC.dataset.likeYn = 'false'
                        likeBtnC.innerText = 'cancel'
                    } else {
                        likeBtnC.dataset.likeYn = 'true'
                        likeBtnC.innerText = 'like'
                    }
                })
            }

            //게시글댓글
            document.querySelector('.lv1_comment_btn').addEventListener('click', (e) => {
                const data = {}
                data.articleNo = e.currentTarget.dataset.articleNo;
                data.contents = e.currentTarget.previousElementSibling.value.trim();
                this.insertComment(data)

            })
            //대댓글
            document.querySelectorAll('.comment_contents').forEach((el) => {
                el.addEventListener('click', (e) => {
                    let elClass = e.currentTarget.parentElement.classList
                    console.log(e.currentTarget.parentElement)
                    let selected = elClass.contains('unselected')
                    console.log(selected)
                    document.querySelectorAll('.selected')
                        .forEach(el => el.classList.replace('selected', 'unselected'))
                    if (selected) {
                        elClass.replace('unselected', 'selected')
                    }
                })
            })
            document.querySelectorAll('.comment_btn').forEach((el) => {
                el.addEventListener('click', (e) => {
                    const data = e.currentTarget.dataset
                    data.contents = e.currentTarget.parentElement.getElementsByClassName('comment_textarea')[0].value.trim()

                    console.log(data)
                    this.insertComment(data);
                });
            })
        }
    }
    insertComment(data){
        let token = document.cookie.match("access_cookie")
        axios.post(COMMENT_URL, JSON.stringify(data), {headers: {"Content-Type": `application/json`, 'Authorization': 'Bearer '+token}})
            .then(res=>{

                //TODO 댓글 영역만 리로딩
                console.log(res)
                window.location.reload();
            })
    }
    scroll(){
        const $ul=document.querySelector('ul')
        let last_li=document.querySelector('li:last-child');
        // let count = $ul.children.length
        const observer = new IntersectionObserver((entries,observer)=>{
            const target = entries[0].target;

            if(entries[0].isIntersecting){
                console.log("현재보이는타겟", target)
                observer.unobserve(last_li)
                //li 추가
                observer.observe(last_li)
            }
        }, {threshold: 0.8});
        observer.observe(last_li);
    }


    pic_view(){

        // $(document).ready(()=>{
        //     axios.get("shoplist", {
        //         responseType : "json"
        //     }).then((response)=> {
        //         console.log(response)
        //         latlng=response.data;
        //         render_map()
        //     });
        // })

        $(document).ready(()=>{
            axios.post(`/board/shop/list`)
                .then((res) => {
                    latlng=res.data;
                    console.log("res->",res.data);
                }).then(
                    render_map()
                )
        })

        function render_map(){
            var container = document.getElementById('map');
            if(container===null || container===undefined){return null;}
            var options = {
                center: new kakao.maps.LatLng(35.13863521918976, 126.91376115137437),
                level: 3
            };
            var map = new kakao.maps.Map(container, options);
            var content = null;
            let positions=[];
            // if(latlng.length>0) {
            //     for (let i = 0; i < latlng.length; i++) {
            //         positions.push({
            //             title: `test` + i,
            //             latlng: new kakao.maps.LatLng(latlng[i].shopLat, latlng[i].shopLong)
            //         })
            //     }
            // }

//---마커이벤트
            let marker = new kakao.maps.Marker({position: map.getCenter()});
            marker.setMap(map);
            kakao.maps.event.addListener(map, 'click', function(mouseEvent) {
                let _latlng = mouseEvent.latLng;
                marker.setPosition(_latlng);

                $('input[name=shopLat]').attr('value', _latlng.getLat());
                $('input[name=shopLong]').attr('value', _latlng.getLng());
                    console.log(_latlng);
                //->폼데이터 변경
                //위도: _latlng.getLat()
                //경도: _latlng.getLng()

            });

            // var positions = [
            //     {
            //         title: 'test',
            //         latlng: new kakao.maps.LatLng(latlng[0].shopLat, latlng[0].shopLong),
            //
            //     },
            //     {
            //         title: 'test2',
            //         latlng: new kakao.maps.LatLng(latlng[1].shopLat, latlng[1].shopLong),
            //
            //     },
            //     {
            //         title: 'test3',
            //         latlng: new kakao.maps.LatLng(latlng[2].shopLat, latlng[2].shopLong)
            //     },
            //     {
            //         title: 'test4',
            //         latlng: new kakao.maps.LatLng(latlng[3].shopLat, latlng[3].shopLong)
            //     }
            // ];
            var imageSrc = "https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/markerStar.png";

            for (var i = 0; i < positions.length; i ++) {

                // 마커 이미지의 이미지 크기 입니다
                var imageSize = new kakao.maps.Size(24, 35);

                // 마커 이미지를 생성합니다
                var markerImage = new kakao.maps.MarkerImage(imageSrc, imageSize);

                // 마커를 생성합니다
                marker = new kakao.maps.Marker({
                    map: map, // 마커를 표시할 지도
                    position: positions[i].latlng, // 마커를 표시할 위치
                    title: positions[i].title, // 마커의 타이틀, 마커에 마우스를 올리면 타이틀이 표시됩니다
                    image: markerImage, // 마커 이미지
                });
                var customOverlay = new kakao.maps.CustomOverlay({
                    position: positions[i].latlng,
                    content: positions[i].content
                });
                customOverlay.setMap(map);
            }
            function panTo(x,y) {
                map.panTo( new kakao.maps.LatLng(x,y));

            }
            $('.title').on('click',function(ev) {
                ev.stopPropagation();
                idx = Number($(this).attr('data-key'))
                let clat=null;
                let clng=null;
                console.log(idx);
                if(latlng!=null&&latlna!==undefined) {
                    for (let i = 0; i < latlng.length; i++) {
                        if (latlng[i].idx === idx) {
                            clat = latlng[i].shopLat;
                            clng = latlng[i].shopLong;
                        }
                    }
                }
                panTo(clat, clng)
            })

        }

    }
    nav_down(){
        let didScroll;
        let lastScrollTop = 0;
        let delta = 5;
        let navbarHeight = $('.header').outerHeight();

        $(window).scroll(function(event){
            didScroll = true;
        });

        setInterval(function() {
            if (didScroll) {
                hasScrolled();
                didScroll = false;
            }
        }, 250);

        function hasScrolled() {
            var st = $(this).scrollTop();

            if(Math.abs(lastScrollTop - st) <= delta)
                return;

            if (st > lastScrollTop && st > navbarHeight){
                $('header').removeClass('nav-down').addClass('nav-up');
            } else if(st + $(window).height() < $(document).height()) {
                $('header').removeClass('nav-up').addClass('nav-down');
            }
            lastScrollTop = st;
        }
    }








    // page_write(){
    //     $('.btn-write').on('click',function(){
    //
    //         axios.get("/boardForm.do", {
    //
    //         }).then((response)=> {
    //             $('.boardContent').html('');
    //             $('.boardContent').append(response.data);
    //         });
    //     })
    // }
    page_edit(){
        $(document).on('click', '.btn-edit', ()=>{
            console.log("btnactive");
            axios.get("/boardUpdate.do/", {
                params: {
                    idx: idx
                },
                headers:["content-type: image/jpeg",
                    "responseType : arraybuffer"],
            }).then((response)=> {
                $('.boardContent').html('');
                $('.boardContent').append(response.data);


            });
        })
    }
    page_edit_submit(){
        $(document).on('click', '.btn-edit-submit', ()=>{
            console.log("btnactive");
            axios.get("/boardInsert.do/", {
                params: {
                    title: title,
                    content: content,
                    writer: writer
                }
            }).then((response)=> {
                $('.boardContent').html('');
                $('.boardContent').append(response.data);
            });
        })
    }
    file_upload(){
        $(document).on('click', '.btn-edit-submit', ()=> {
            const formData = new FormData();
            console.log(formData);

            const file = document.getElementById("files");
            console.log("file",file);
            // if(fileList.files.length>0){
            // }
            formData.append("file", file.files[0]);

            axios.post("/insertProc", formData, {
                headers: {"Content-Type": "multipart/form-data"},
                // contentType: false,
                // processData: false,
            }).then((response)=>{
                $('.boardContent').html('');
                $('.boardContent').append(response.data);
            });
        })
    }
    page_delete(){
        $(document).on('click','.btn-del', ()=>{
            console.log("btnactive");
            axios.get("/boardDelete.do", {
                params: {
                    idx: idx
                }
            }).then(()=> {
                $('.boardContent').html('');
            });
        })
    }
    // page_load() {
    //     console.log('aaaaaaaaaa');
    //     console.log('ddddddd')
    //
    //
    //     $('.title').on('click',function() {
    //         //람다는 this 불가
    //         idx = $(this).attr('data-key')
    //         axios.get("/getImage", {
    //             params: { idx: idx },
    //             responseType :'arraybuffer',
    //         }).then((response)=> {
    //             const audioContext = new AudioContext()
    //             const audioBuffer = audioContext.decodeAudioData(response.data);
    //             let audioBufferSourceNode = audioContext.createBufferSource();
    //             audioBufferSourceNode.buffer = audioBuffer;
    //             let gainNode = new GainNode(audioContext);
    //             gainNode.gain.value = 0.5;
    //             audioBufferSourceNode.connect(gainNode).connect(audioContext.destination);
    //
    //
    //             $('.boardContent').html('');
    //             $('.boardContent').append('<audio src="" ></audio><button data-playing="false" role="switch" aria-checked="false">\n' +
    //                 '            <span>Play/Pause</span>\n' +
    //                 '        </button>');
    //             const audioElement = document.querySelector('audio');
    //             const track = audioContext.createMediaElementSource(audioElement);
    //             track.connect(audioContext.destination);
    //         });
    //     })
    // };



    // audio_test1111(){
    //
    //     const myAudio = new Audio();
    //     myAudio.src = "sound.mp3";
    //     myAudio.play();
    //
    // }










}
