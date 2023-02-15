const axios = require('axios');

    export const insertArticle=(e)=>{
        const formData = new FormData(e);
        axios
            .post(`board/ins/`, formData, {
                headers: {
                    // "X-AUTH-TOKEN": token,
                    "Content-Type": `multipart/form-data`,
                },
            })
            .then((res) => console.log(res));

            }
    export const likeArticle=(articleNo)=>{
        let url=`/api/articles/${articleNo}/like`
        return axios.put(url)
    }
    export const CancelLike=(articleNo)=>{
        let url=`/api/articles/${articleNo}/like`
        return axios.delete(url)
    }


