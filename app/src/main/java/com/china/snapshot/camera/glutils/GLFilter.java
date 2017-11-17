package com.china.snapshot.camera.glutils;

import java.util.ArrayList;


public class GLFilter {
    private Filter filtercur=null;
    private ArrayList<Filter> aryfilter=new ArrayList<Filter>();
    private static int filterindex=0;
    private int filternum=8;

    public void setFlag(int ff){
        if(ff==3){
            filterindex=10;
        }else if(ff==4){
            filterindex=0;
        } else{
            if(ff>0){
                filterindex++;
                if(filterindex>=filternum)
                    filterindex=0;
            }
            else{
                filterindex--;
                if(filterindex<0){
                    filterindex=filternum-1;
                }
            }
        }
        CameraFile.filterindex=filterindex;
        if(filtercur!=null)
            filtercur.setFlag(filterindex);

    }

    public void setMatrix(final float[] matrix, final int offset) {
        if(filtercur!=null)
             filtercur.setMatrix(matrix,offset);
    }

    public  static void deleteTex() {
        Filter.deleteTex();
    }


    public GLFilter(){
        aryfilter.clear();
        filterindex=CameraFile.filterindex;
        filtercur=getFilter(0);
        filtercur.setFlag(filterindex);
    }

    public Filter getFilter(int index){
        switch (index){
            case 0:
                return new GLFilter0();
            default:
                break;
        }
        return null;
    }

    public  static int initTex() {
        return Filter.initTex();
    }

    public void release(){
        if(filtercur!=null)
            filtercur.release();
    }

    public void draw(final int tex_id, final float[] tex_matrix,float singlestep1,float singlestep2) {
            if (filtercur != null)
                filtercur.draw(tex_id, tex_matrix, 0, 0);
    }
}
