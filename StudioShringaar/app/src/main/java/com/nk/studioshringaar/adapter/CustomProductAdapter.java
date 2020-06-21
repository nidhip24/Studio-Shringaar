package com.nk.studioshringaar.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.flexbox.FlexboxLayout;
import com.nk.studioshringaar.R;
import com.nk.studioshringaar.SliderAdapterExample;
import com.nk.studioshringaar.data.Discount;
import com.nk.studioshringaar.data.DiscountData;
import com.nk.studioshringaar.ui.category.Category;
import com.nk.studioshringaar.ui.product.ProductDetail;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CustomProductAdapter extends RecyclerView.Adapter<CustomProductAdapter.ViewHolder> {

    private Activity activity;
    private LayoutInflater inflater;
    private List<ProductsData> pData;
    ArrayList<Discount> discountList;

    public CustomProductAdapter(Activity activity, List<ProductsData> en) {
        this.activity = activity;
        this.pData = en;
        //getting discount
        DiscountData dd = new DiscountData(activity);
        discountList = dd.getDiscount();
    }

    @Override
    public CustomProductAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType==0){
            View view = LayoutInflater.from(activity).inflate(R.layout.scrollview_content, parent, false);
            return new CustomProductAdapter.ViewHolder(view);
        }else if (viewType==1){
            View view = LayoutInflater.from(activity).inflate(R.layout.home_product, parent, false);
            return new CustomProductAdapter.ViewHolder(view);
        }else if(viewType==3) {
            View view = LayoutInflater.from(activity).inflate(R.layout.poster_layout, parent, false);
            return new CustomProductAdapter.ViewHolder(view);
        }else{
            View view = LayoutInflater.from(activity).inflate(R.layout.slider_layout, parent, false);
            return new CustomProductAdapter.ViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        ProductsData p = pData.get(position);
        return p.getLayout_type();
    }

    @Override
    public void onBindViewHolder(CustomProductAdapter.ViewHolder holder, int position) {
        ProductsData p = pData.get(position);
//        holder.title.setText(p.getName());

//        holder.title.setText(arrayList.get(position));
        if (p.getLayout_type()==0){
            Log.d("ProductAdapter","0");

            holder.title.setText(p.getTitle());

            Display display = activity.getWindowManager().getDefaultDisplay();
            Point size = new Point();
            try {
                display.getRealSize(size);
            } catch (NoSuchMethodError err) {
                display.getSize(size);
            }
            int width = size.x;
            int height = size.y;

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams((int)(width/2)-50, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(8, 0, 8, 5);

            List<Product> pp = p.getP();

            for (int i = 0; i < pp.size(); i++) {
                final Product temp = pp.get(i);

                View child = activity.getLayoutInflater().inflate(R.layout.product_view, null);
                child.setLayoutParams(layoutParams);

                ImageView product_img = child.findViewById(R.id.product_img);
                Glide.with(activity)
                        .load(temp.getImg())
                        .into(product_img);

                TextView prod_name = child.findViewById(R.id.product_name);
                prod_name.setText(temp.getName());

                TextView prod_price = child.findViewById(R.id.product_price);
//                prod_price.setText("\u20B9"+temp.getPrice());

                //setting discount if available
                TextView product_original_price = child.findViewById(R.id.product_original_price);
                long discount = -1;
                long maxDiscount = 0;
                for (int ii=0; ii < discountList.size(); ii++) {
                    Discount d = discountList.get(ii);
                    Log.e("DISOCUNTFINDING", temp.getCategory()+" before  accct   " + d.getCat());
                    if (temp.getCategory().equalsIgnoreCase(d.getCat()) || d.getCat().equalsIgnoreCase("all")) {
                        Log.e("DISOCUNTFINDING", temp.getCategory()+"  accct   " + d.getCat());
                        if (discount < d.getDiscount()) {
                            discount = d.getDiscount();
                            maxDiscount = d.getMaxDiscount();
                        }
                    }
                }

                if (discount!=-1){
                    int pr = Integer.parseInt(temp.getPrice());
                    int d = (pr*(int)discount)/100;
                    if (d>maxDiscount){
                        d= (int) maxDiscount;
                    }
                    DecimalFormat formatter = new DecimalFormat("#,###,###");
                    long amttemp = Long.parseLong(temp.getPrice());
                    prod_price.setText("\u20B9"+ formatter.format(amttemp-d) );
                    product_original_price.setText("\u20B9"+ temp.getPrice());
                    product_original_price.setVisibility(View.VISIBLE);
                }else{
                    DecimalFormat formatter = new DecimalFormat("#,###,###");
                    long amttemp = Long.parseLong(temp.getPrice());
                    prod_price.setText("\u20B9"+ formatter.format(amttemp) );
                    product_original_price.setVisibility(View.GONE);
                }

                child.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(activity, ProductDetail.class);
                        i.putExtra("id", temp.getId());
                        i.putExtra("cat", temp.getCategory());
                        activity.startActivity(i);
                    }
                });

                holder.myRoot.addView(child);
            }

        }else if (p.getLayout_type()==1){

            Log.d("ProductAdapter","1");
            holder.title.setText(p.getTitle());
            List<Product> pp = p.getP();

            if (pp.size()==3){

                Product temp = pp.get(0);
                Glide.with(activity)
                        .load(temp.getImg())
                        .into(holder.img1);

                final Product finalTemp3 = temp;
                holder.view_all.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(activity, Category.class);
                        i.putExtra("name", finalTemp3.getCategory());
                        activity.startActivity(i);
                    }
                });

                long discount = -1;
                long maxDiscount = 0;
                for (int ii=0; ii < discountList.size(); ii++) {
                    Discount d = discountList.get(ii);
//                    Log.e("DISOCUNTFINDING", temp.getCategory()+" before  accct   " + d.getCat());
                    if (temp.getCategory().equalsIgnoreCase(d.getCat()) || d.getCat().equalsIgnoreCase("all")) {
//                        Log.e("DISOCUNTFINDING", temp.getCategory()+"  accct   " + d.getCat());
                        if (discount < d.getDiscount()) {
                            discount = d.getDiscount();
                            maxDiscount = d.getMaxDiscount();
                        }
                    }
                }

                holder.img1_title.setText(temp.getName());
                DecimalFormat formatter = new DecimalFormat("#,###,###");
                if (discount!=-1){
                    int pr = Integer.parseInt(temp.getPrice());
                    int d = (pr*(int)discount)/100;
                    if (d>maxDiscount){
                        d= (int) maxDiscount;
                    }
                    long amttemp = Long.parseLong(temp.getPrice());
                    holder.img1_price.setText("\u20B9"+ formatter.format(amttemp-d) );
                    holder.product_original_price1.setText("\u20B9"+ temp.getPrice());
                    holder.product_original_price1.setVisibility(View.VISIBLE);
                }else{
                    long amttemp = Long.parseLong(temp.getPrice());
                    holder.img1_price.setText("\u20B9"+ formatter.format(amttemp) );
                    holder.product_original_price1.setVisibility(View.GONE);
                }
//                holder.img1_price.setText("\u20B9"+temp.getPrice());

                final Product finalTemp = temp;
                holder.img1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(activity, ProductDetail.class);
                        i.putExtra("id", finalTemp.getId());
                        i.putExtra("cat", finalTemp.getCategory());
                        activity.startActivity(i);
                    }
                });
                holder.img1_title.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(activity, ProductDetail.class);
                        i.putExtra("id", finalTemp.getId());
                        i.putExtra("cat", finalTemp.getCategory());
                        activity.startActivity(i);
                    }
                });
                holder.img1_price.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(activity, ProductDetail.class);
                        i.putExtra("id", finalTemp.getId());
                        i.putExtra("cat", finalTemp.getCategory());
                        activity.startActivity(i);
                    }
                });

                temp = pp.get(1);
                Glide.with(activity)
                        .load(temp.getImg())
                        .into(holder.img2);
                holder.img2_title.setText(temp.getName());
//                holder.img2_price.setText("\u20B9"+temp.getPrice());

                if (discount!=-1){
                    int pr = Integer.parseInt(temp.getPrice());
                    int d = (pr*(int)discount)/100;
                    if (d>maxDiscount){
                        d= (int) maxDiscount;
                    }
                    long amttemp = Long.parseLong(temp.getPrice());
                    holder.img2_price.setText("\u20B9"+ formatter.format(amttemp-d) );
                    holder.product_original_price2.setText("\u20B9"+ temp.getPrice());
                    holder.product_original_price2.setVisibility(View.VISIBLE);
                }else{
                    long amttemp = Long.parseLong(temp.getPrice());
                    holder.img2_price.setText("\u20B9"+ formatter.format(amttemp) );
                    holder.product_original_price2.setVisibility(View.GONE);
                }


                final Product finalTemp1 = temp;
                holder.img2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(activity, ProductDetail.class);
                        i.putExtra("id", finalTemp1.getId());
                        i.putExtra("cat", finalTemp1.getCategory());
                        activity.startActivity(i);
                    }
                });
                holder.img2_title.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(activity, ProductDetail.class);
                        i.putExtra("id", finalTemp1.getId());
                        i.putExtra("cat", finalTemp1.getCategory());
                        activity.startActivity(i);
                    }
                });
                holder.img2_price.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(activity, ProductDetail.class);
                        i.putExtra("id", finalTemp1.getId());
                        i.putExtra("cat", finalTemp1.getCategory());
                        activity.startActivity(i);
                    }
                });

                temp = pp.get(2);
                Glide.with(activity)
                        .load(temp.getImg())
                        .into(holder.img3);
                holder.img3_title.setText(temp.getName());
//                holder.img3_price.setText("\u20B9"+temp.getPrice());

                if (discount!=-1){
                    int pr = Integer.parseInt(temp.getPrice());
                    int d = (pr*(int)discount)/100;
                    if (d>maxDiscount){
                        d= (int) maxDiscount;
                    }
                    long amttemp = Long.parseLong(temp.getPrice());
                    holder.img3_price.setText("\u20B9"+ formatter.format(amttemp-d) );
                    holder.product_original_price3.setText("\u20B9"+ temp.getPrice());
                    holder.product_original_price3.setVisibility(View.VISIBLE);
                }else{
                    long amttemp = Long.parseLong(temp.getPrice());
                    holder.img3_price.setText("\u20B9"+ formatter.format(amttemp) );
                    holder.product_original_price3.setVisibility(View.GONE);
                }

                final Product finalTemp2 = temp;
                holder.img3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(activity, ProductDetail.class);
                        i.putExtra("id", finalTemp2.getId());
                        i.putExtra("cat", finalTemp2.getCategory());
                        activity.startActivity(i);
                    }
                });
                holder.img3_title.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(activity, ProductDetail.class);
                        i.putExtra("id", finalTemp2.getId());
                        i.putExtra("cat", finalTemp2.getCategory());
                        activity.startActivity(i);
                    }
                });
                holder.img3_price.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(activity, ProductDetail.class);
                        i.putExtra("id", finalTemp2.getId());
                        i.putExtra("cat", finalTemp2.getCategory());
                        activity.startActivity(i);
                    }
                });

            }

        }else if(p.getLayout_type()==3) {
            Log.d("ProductAdapter","poster 3");
//            holder.poster
            Glide.with(activity)
                    .load(p.getImg1())
                    .into(holder.poster);
//            convertView = inflater.inflate(R.layout.poster_layout, null);
        }else{

            Log.d("ProductAdapter","eles");
            holder.sliderView.setIndicatorAnimation(IndicatorAnimations.DROP); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
            holder.sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
            holder.sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
            holder.sliderView.setIndicatorSelectedColor(Color.WHITE);
            holder.sliderView.setIndicatorUnselectedColor(Color.GRAY);
            holder.sliderView.setScrollTimeInSec(4); //set scroll delay in seconds :
            holder.sliderView.startAutoCycle();
            List<String> imgList = new ArrayList<String>();
            imgList.add(p.getImg1());
            imgList.add(p.getImg2());
            imgList.add(p.getImg3());
            imgList.add(p.getImg4());
            holder.sliderView.setSliderAdapter(new SliderAdapterExample(activity,imgList));
        }

    }

    @Override
    public int getItemCount() {
        return pData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title,price;
        CardView c;
        SliderView sliderView;

        ImageView img1, img2, img3, poster;
        TextView img1_title, img1_price, product_original_price1;
        TextView img2_title, img2_price, product_original_price2;
        TextView img3_title, img3_price, product_original_price3;

        Button view_all;
        FlexboxLayout myRoot;

        public ViewHolder(View itemView) {
            super(itemView);

            myRoot = itemView.findViewById(R.id.product_list);

            title = itemView.findViewById(R.id.title);
            img1 = itemView.findViewById(R.id.img1);
            img1_title = itemView.findViewById(R.id.img1_title);
            img1_price = itemView.findViewById(R.id.img1_price);
            product_original_price1 = itemView.findViewById(R.id.product_original_price1);

            img2 = itemView.findViewById(R.id.img2);
            img2_title = itemView.findViewById(R.id.img2_title);
            img2_price = itemView.findViewById(R.id.img2_price);
            product_original_price2 = itemView.findViewById(R.id.product_original_price2);

            img3 = itemView.findViewById(R.id.img3);
            img3_title = itemView.findViewById(R.id.img3_title);
            img3_price = itemView.findViewById(R.id.img3_price);
            product_original_price3 = itemView.findViewById(R.id.product_original_price3);

            view_all = itemView.findViewById(R.id.tic);

            price = itemView.findViewById(R.id.product_price);
            c = itemView.findViewById(R.id.card_view);
            sliderView = itemView.findViewById(R.id.imageSlider);

            poster = itemView.findViewById(R.id.poster);

        }
    }
}
