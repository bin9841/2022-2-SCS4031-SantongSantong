package com.example.frontend;



public class PostItem {
    private String area_id;
    private String image;

    public String getArea(){
        return area_id;
    }
    public String setArea(String id){
        area_id = id;
        return id;
    }
    public String getImg(){
        return image;
    }
    public String setImg(String img){
        image = img;
        return img;
    }


}
