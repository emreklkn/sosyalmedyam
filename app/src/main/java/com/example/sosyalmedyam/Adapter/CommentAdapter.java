package com.example.sosyalmedyam.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sosyalmedyam.MainActivity;
import com.example.sosyalmedyam.Model.Comment;
import com.example.sosyalmedyam.Model.User;
import com.example.sosyalmedyam.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private Context mContext;
    private List<Comment> mComments;

    String postId;

    private FirebaseUser fUser;

    public CommentAdapter(Context mContext, List<Comment> mComments , String postId) {
        this.mContext = mContext;
        this.mComments = mComments;
        this.postId = postId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //*Bu kod, bir RecyclerView için bir ViewHolder oluşturmak için kullanılan onCreateViewHolder metodunun bir parçasıdır.
        // Bu metod, RecyclerView için yorumları oluşturmak için kullanılan bir arayüz sağlar.
        View view = LayoutInflater.from(mContext).inflate(R.layout.comment_item, parent, false);
        return new CommentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        //*Bu kod, RecyclerView için bir görünümün içeriğini doldurmak için kullanılan onBindViewHolder metodunun bir parçasıdır.
        // Bu metod, RecyclerView için oluşturulan her bir görünüm için çağrılır ve görünümün içeriğini doldurur.

        fUser = FirebaseAuth.getInstance().getCurrentUser();

        final Comment comment = mComments.get(position);

        holder.comment.setText(comment.getComment());

        FirebaseDatabase.getInstance().getReference().child("Users").child(comment.getPublisher()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //*Bu kod, Firebase veritabanından yayıncının kullanıcı bilgilerini çekmek için kullanılan onDataChange() metodunun bir parçasıdır.
                // Bu metod, Firebase veritabanından çekilen verilerin değiştiğinde veya ilk defa çekildiğinde çağrılır.
                User user = dataSnapshot.getValue(User.class);

                holder.username.setText(user.getUsername());//*Bu kod, kullanıcının kullanıcı adını (username) holder.username nesnesine set etmek için kullanılır
                if (user.getImageurl().equals("default")) {
                    holder.imageProfile.setImageResource(R.mipmap.ic_launcher);
                } else {
                    Picasso.get().load(user.getImageurl()).into(holder.imageProfile);
                }
                ///* ayrıca Bu kod, veritabanından çekilen verileri User sınıfına dönüştürerek User nesnesi oluşturur. Daha sonra, kullanıcının username'i holder.username nesnesine set edilir. Eğer kullanıcının profil resmi varsa, kullanıcının imageurl'si kullanılarak Picasso kütüphanesi ile profil resmi holder.imageProfile nesnesine yüklenir. Eğer profil resmi yoksa, default olarak belirlenmiş bir resim holder.imageProfile nesnesine atanır.
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //*Bu kod, yorumun üzerine tıklandığında gerçekleşecek olayları tanımlar
                Intent intent = new Intent(mContext, MainActivity.class);
                intent.putExtra("publisherId", comment.getPublisher());
                mContext.startActivity(intent);
            }
        });

        holder.imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //*Bu kod, yorumun yayıncısının profil resmine tıklandığında gerçekleşecek olayları tanımlar.
                Intent intent = new Intent(mContext, MainActivity.class);
                intent.putExtra("publisherId", comment.getPublisher());
                mContext.startActivity(intent);
            }
        });
        //* yorumun üzerinde silme aşamasına başlıyoruz
        //* aşağıdaki kod oişe yarar

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) { //* uzun süre basıldıysa yorumun üzerine
                if (comment.getPublisher().endsWith(fUser.getUid())) { //* kullanıcı kendisiyse sorar değilse sormaz
                    AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                    alertDialog.setTitle("Do you want to delete?");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, int which) { //* yes dendiğinde silme işlemi firebase den çağrılıp
                            FirebaseDatabase.getInstance().getReference().child("Comments")
                                    .child(postId).child(comment.getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) { //* basarılı ise silme
                                                Toast.makeText(mContext, "Comment deleted successfully!", Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                            }
                                        }
                                    });
                        }
                    });

                    alertDialog.show();
                }

                return true;
            };
        });

    }

    @Override
    public int getItemCount() {
        return mComments.size();
    } //* yorumları getirir

    public class ViewHolder extends RecyclerView.ViewHolder{
        //*RecyclerView için oluşturulan ViewHolder sınıfını tanımlar.
        // ViewHolder, RecyclerView içinde yorumları depolamak için kullanılan bir nesnedir

        public CircleImageView imageProfile;
        public TextView username;
        public TextView comment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //* burda u-yorumları topluyoruz
            imageProfile = itemView.findViewById(R.id.image_profile);
            username = itemView.findViewById(R.id.username);
            comment = itemView.findViewById(R.id.comment);
        }
    }

}
