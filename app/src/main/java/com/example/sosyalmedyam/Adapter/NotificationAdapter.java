package com.example.sosyalmedyam.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sosyalmedyam.Fragments.PostDetailFragment;
import com.example.sosyalmedyam.Fragments.ProfileFragment;
import com.example.sosyalmedyam.Model.Notification;
import com.example.sosyalmedyam.Model.Post;
import com.example.sosyalmedyam.Model.User;
import com.example.sosyalmedyam.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private Context mContext;
    private List<Notification> mNotifications;

    public NotificationAdapter(Context mContext, List<Notification> mNotifications) {
        //*NotificationAdapter sınıfının constructor metodunu tanımlar.
        // Constructor metod, NotificationAdapter sınıfının nesnelerini oluştururken çağrılır ve gerekli değişkenleri alır.
        this.mContext = mContext;
        this.mNotifications = mNotifications;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //*Bu metod, RecyclerView için her bir görünüm için bir ViewHolder nesnesi oluşturur.
        View view = LayoutInflater.from(mContext).inflate(R.layout.notification_item, parent, false);

        return new NotificationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //*RecyclerView içinde görünen her bildirim için veri bağlama işlemi yapar. Bu metod,
        // RecyclerView tarafından belirli bir pozisyondaki verilerin görüntülenmesi için çağrılır.

        final Notification notification = mNotifications.get(position);

        getUser(holder.imageProfile, holder.username, notification.getUserid());
        holder.comment.setText(notification.getText());

        if (notification.isIsPost()) {
            holder.postImage.setVisibility(View.VISIBLE);
            getPostImage(holder.postImage, notification.getPostid());
        } else {
            holder.postImage.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            //* ViewHolder'ın itemView'ına bir OnClickListener atar. Bu sayede, ViewHolder'ın itemView'ına
            // tıklandığında, bildirimin bir postla ilişkili olup olmadığı kontrol edilir. Eğer ilişkili ise,
            // kullanıcının SharedPreferences'ındaki "postid" anahtarına bildirimin postid'si atanır ve
            // PostDetailFragment ekranına geçiş yapılır. Eğer ilişkili değilse, kullanıcının SharedPreferences'ındaki "profileId"
            // anahtarına bildirimin userid'si atanır ve ProfileFragment ekranına geçiş yapılır.
            @Override
            public void onClick(View v) {
                if (notification.isIsPost()) {
                    mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
                            .edit().putString("postid", notification.getPostid()).apply();

                    ((FragmentActivity)mContext).getSupportFragmentManager()
                            .beginTransaction().replace(R.id.fragment_container, new PostDetailFragment()).commit();
                } else {
                    mContext.getSharedPreferences("PROFILE", Context.MODE_PRIVATE)
                            .edit().putString("profileId", notification.getUserid()).apply();

                    ((FragmentActivity)mContext).getSupportFragmentManager()
                            .beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mNotifications.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        //*ViewHolder, RecyclerView içinde görünen tek bir satırdaki görünümlerin referanslarını tutan bir nesnedir

        public ImageView imageProfile;
        public ImageView postImage;
        public TextView username;
        public TextView comment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //* burda gerekli bilgiler tutuluyor

            imageProfile = itemView.findViewById(R.id.image_profile);
            postImage = itemView.findViewById(R.id.post_image);
            username = itemView.findViewById(R.id.username);
            comment = itemView.findViewById(R.id.comment);
        }
    }

    private void getPostImage(final ImageView imageView, String postId) {
        FirebaseDatabase.getInstance().getReference().child("Posts").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Post post = dataSnapshot.getValue(Post.class);
                Picasso.get().load(post.getImageurl()).placeholder(R.mipmap.ic_launcher).into(imageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getUser(final ImageView imageView, final TextView textView, String userId) {
        FirebaseDatabase.getInstance().getReference().child("Users").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user.getImageurl().equals("default")) {
                    imageView.setImageResource(R.mipmap.ic_launcher);
                } else {
                    Picasso.get().load(user.getImageurl()).into(imageView);
                }
                textView.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
