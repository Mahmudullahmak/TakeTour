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
        import android.widget.PopupMenu;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.android.shamim.taketour.AddEvent;
        import com.android.shamim.taketour.EventDetail;
        import com.android.shamim.taketour.EventList;
        import com.android.shamim.taketour.ExpenditureList;
        import com.android.shamim.taketour.R;
        import com.android.shamim.taketour.pojjoclass.Events;
        import com.android.shamim.taketour.pojjoclass.Expenditure;
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

public class ExpenditureAdapter extends RecyclerView.Adapter<ExpenditureAdapter.ExpenseViewHolder>{
    private Context context;
    private ArrayList<Expenditure> expenses;
    private int count = 0;


    public ExpenditureAdapter(Context context, ArrayList<Expenditure> expenses){
        this.context = context;
        this.expenses = expenses;

    }

    @Override
    public ExpenseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.single_expenditure_row,parent,false);
        return new ExpenseViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ExpenseViewHolder holder, int position) {
        final Expenditure expens = expenses.get(position);


        holder.extitle.setText(expenses.get(position).getDescription());
        holder.excost.setText("Tk. "+String.valueOf(expenses.get(position).getExpense())+"0");

        holder.createDate.setText( "On: "+expenses.get(position).getCreatedate());


        holder.optionDigit.setOnClickListener(new View.OnClickListener() {
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
                                    ((ExpenditureList) v.getContext()).editExpenseDialog(expens);
                                } catch (Exception e) {
                                    // ignore
                                }

                                break;
                            case R.id.delete:
                                try {
                                    ((ExpenditureList) v.getContext()).deleteRecord(expens);
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

        holder.excost.setOnClickListener(new View.OnClickListener() {
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
                                    ((ExpenditureList) v.getContext()).editExpenseDialog(expens);
                                } catch (Exception e) {
                                    // ignore
                                }

                                break;
                            case R.id.delete:
                                try {
                                    ((ExpenditureList) v.getContext()).deleteRecord(expens);
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

        holder.createDate.setOnClickListener(new View.OnClickListener() {
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
                                    ((ExpenditureList) v.getContext()).editExpenseDialog(expens);
                                } catch (Exception e) {
                                    // ignore
                                }

                                break;
                            case R.id.delete:
                                try {
                                    ((ExpenditureList) v.getContext()).deleteRecord(expens);
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

        holder.extitle.setOnClickListener(new View.OnClickListener() {
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
                                    ((ExpenditureList) v.getContext()).editExpenseDialog(expens);
                                } catch (Exception e) {
                                    // ignore
                                }

                                break;
                            case R.id.delete:
                                try {
                                    ((ExpenditureList) v.getContext()).deleteRecord(expens);
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
        return expenses.size();
    }

    public class ExpenseViewHolder extends RecyclerView.ViewHolder {
        DatabaseReference root;
        private EventAdapter eventAdapter;
        FirebaseUser user;
        private FirebaseAuth auth;
        TextView extitle ;
        TextView excost ;
        TextView optionDigit;
        TextView createDate;
        public ExpenseViewHolder(View itemView) {
            super(itemView);
            extitle = itemView.findViewById(R.id.roweventname);
            excost = itemView.findViewById(R.id.budgetrow);
            createDate = itemView.findViewById(R.id.createtDaterow);
            optionDigit = itemView.findViewById(R.id.optionDigit);


        }

    }
}
