package com.android.shamim.taketour.helper;

        import android.content.Context;
        import android.content.Intent;
        import android.support.annotation.NonNull;
        import android.support.annotation.Nullable;
        import android.support.v7.widget.RecyclerView;
        import android.view.LayoutInflater;
        import android.view.MenuItem;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.AdapterView;
        import android.widget.ArrayAdapter;
        import android.widget.ImageView;
        import android.widget.PopupMenu;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.android.shamim.taketour.AddEvent;
        import com.android.shamim.taketour.EventDetail;
        import com.android.shamim.taketour.EventList;
        import com.android.shamim.taketour.ExpenditureList;
        import com.android.shamim.taketour.FriendList;
        import com.android.shamim.taketour.R;
        import com.android.shamim.taketour.pojjoclass.Events;
        import com.android.shamim.taketour.pojjoclass.Expenditure;
        import com.android.shamim.taketour.pojjoclass.Friends;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.auth.FirebaseUser;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;

        import java.text.ParseException;
        import java.text.SimpleDateFormat;
        import java.util.ArrayList;
        import java.util.Calendar;
        import java.util.Date;
        import java.util.Locale;
        import java.util.concurrent.TimeUnit;

/**
 * Created by SHAMIM on 1/25/2018.
 */

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.FriendViewHolder>{
    private Context context;
    private ArrayList<Friends> friends;
    private int count = 0;


    public FriendsAdapter(Context context, ArrayList<Friends> friends){
        this.context = context;
        this.friends = friends;

    }

    @Override
    public FriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.single_friend_row,parent,false);
        return new FriendViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final FriendViewHolder holder, int position) {
        final Friends friend = friends.get(position);
/*

        int imgdrawable=R.drawable.pcalls;
        holder.pcall.setImageDrawable(context.getResources().getDrawable(imgdrawable));
*/


        holder.friendname.setText(friends.get(position).getFriendName());
        holder.friendphone.setText("Ph. "+String.valueOf(friends.get(position).getFriendPhone()));

        holder.friendemail.setText( "E.: "+friends.get(position).getFriendEmail());
        holder.pcall.setImageDrawable(context.getResources().getDrawable(R.drawable.pcalls));
        holder.psms.setImageDrawable(context.getResources().getDrawable(R.drawable.smss));
        holder.semail.setImageDrawable(context.getResources().getDrawable(R.drawable.emails));

        holder.pcall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ((FriendList) v.getContext()).callPerson(friend.getFriendPhone());
                } catch (Exception e) {
                    // ignore
                }
            }
        });
        holder.psms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ((FriendList) v.getContext()).sendMessage(friend.getFriendPhone());
                } catch (Exception e) {
                    // ignore
                }
            }
        });
        holder.semail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ((FriendList) v.getContext()).sendEmail(friend.getFriendEmail());
                } catch (Exception e) {
                    // ignore
                }
            }
        });
        holder.optionDigit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                PopupMenu popupMenu = new PopupMenu(context,holder.optionDigit);
                popupMenu.inflate(R.menu.option_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){

                            case R.id.edit:
                                try {
                                    ((FriendList) v.getContext()).editFriendDialog(friend);
                                } catch (Exception e) {
                                    // ignore
                                }

                                break;
                            case R.id.delete:
                                try {
                                    ((FriendList) v.getContext()).deleteRecord(friend);
                                } catch (Exception e) {
                                    // ignore
                                }
                                break;

                        }


                        return false;
                    }
                });
                popupMenu.show();
            }
        });

        holder.friendname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                PopupMenu popupMenu = new PopupMenu(context,holder.optionDigit);
                popupMenu.inflate(R.menu.option_menu2);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){

                            case R.id.edit:
                                try {
                                    ((FriendList) v.getContext()).editFriendDialog(friend);
                                } catch (Exception e) {
                                    // ignore
                                }

                                break;
                            case R.id.delete:
                                try {
                                    ((FriendList) v.getContext()).deleteRecord(friend);
                                } catch (Exception e) {
                                    // ignore
                                }
                                break;

                        }

                        return false;
                    }
                });
                popupMenu.show();
            }
        });

        holder.friendemail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                PopupMenu popupMenu = new PopupMenu(context,holder.optionDigit);
                popupMenu.inflate(R.menu.option_menu2);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){

                            case R.id.edit:
                                try {
                                    ((FriendList) v.getContext()).editFriendDialog(friend);
                                } catch (Exception e) {
                                    // ignore
                                }

                                break;
                            case R.id.delete:
                                try {
                                    ((FriendList) v.getContext()).deleteRecord(friend);
                                } catch (Exception e) {
                                    // ignore
                                }
                                break;

                        }


                        return false;
                    }
                });
                popupMenu.show();
            }
        });

        holder.friendphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                PopupMenu popupMenu = new PopupMenu(context,holder.optionDigit);
                popupMenu.inflate(R.menu.option_menu2);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){

                            case R.id.edit:
                                try {
                                    ((FriendList) v.getContext()).editFriendDialog(friend);
                                } catch (Exception e) {
                                    // ignore
                                }

                                break;
                            case R.id.delete:
                                try {
                                    ((FriendList) v.getContext()).deleteRecord(friend);
                                } catch (Exception e) {
                                    // ignore
                                }
                                break;

                        }


                        return false;
                    }
                });
                popupMenu.show();
            }
        });
    }
    @Override
    public int getItemCount() {
        return friends.size();
    }

    public class FriendViewHolder extends RecyclerView.ViewHolder {
        DatabaseReference root;
        private EventAdapter eventAdapter;
        FirebaseUser user;
        private FirebaseAuth auth;
        TextView friendname ;
        TextView friendemail ;
        TextView friendphone;
        TextView optionDigit;
        ImageView pcall;
        ImageView psms;
        ImageView semail;
        public FriendViewHolder(View itemView) {
            super(itemView);
            friendname = itemView.findViewById(R.id.friendname);
            friendemail = itemView.findViewById(R.id.email);
            friendphone = itemView.findViewById(R.id.phone);
            optionDigit = itemView.findViewById(R.id.optionDigit);
            pcall = itemView.findViewById(R.id.phonecall);
            psms = itemView.findViewById(R.id.sendsms);
            semail = itemView.findViewById(R.id.sendemail);


        }

    }
}
