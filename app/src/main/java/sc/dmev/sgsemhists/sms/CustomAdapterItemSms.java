package sc.dmev.sgsemhists.sms;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.List;
import sc.dmev.sgsemhists.R;

public class CustomAdapterItemSms extends RecyclerView.Adapter<CustomAdapterItemSms.ViewHolder> {

    private Context mContext;
    private List<ModelSms> mModleSms;
    private OnItemClickListener clickListener;
    private OnLogItemClickListener logItemClickListener;

    public CustomAdapterItemSms(Context mContext, List<ModelSms> mModleSms) {
        this.mContext = mContext;
        this.mModleSms = mModleSms;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.item_sms, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(rootView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.tvSmsFrom.setText(mModleSms.get(position).getFrom());
        viewHolder.tvSmsTo.setText(mModleSms.get(position).getTo());
        viewHolder.tvSmsMsg.setText(mModleSms.get(position).getMsg());
        viewHolder.tvSmsDateTime.setText(mModleSms.get(position).getDate() + " " + mModleSms.get(position).getTime());
        viewHolder.tvMsgError.setText("* " + mModleSms.get(position).getMsgError());
        if (mModleSms.get(position).getMsgError().length() > 0){
            viewHolder.tvMsgError.setVisibility(View.VISIBLE);
        }else {
            viewHolder.tvMsgError.setVisibility(View.GONE);
        }

        if (mModleSms.get(position).getSented() == 0){
            viewHolder.lnContentMsm.setBackgroundResource(R.color.color_bg_not_sent_sms);
            viewHolder.tvMsgError.setText("* " + mModleSms.get(position).getMsgError());
            viewHolder.tvMsgError.setTextColor(mContext.getResources().getColor(R.color.color_error_no_sent));
        }else {
            viewHolder.lnContentMsm.setBackgroundResource(R.color.color_bg_sent_sms);
            viewHolder.tvMsgError.setTextColor(mContext.getResources().getColor(R.color.color_error_sent));
        }

        viewHolder.lnContentItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListener.onItemClick(view,position);
            }
        });
        viewHolder.lnContentItem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                logItemClickListener.onItemLongClickListener(view,position);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mModleSms.size();
    }


   public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView tvSmsFrom,tvSmsTo,tvSmsMsg,tvSmsDateTime,tvMsgError;
        public LinearLayout lnContentItem,lnContentMsm;
        public ViewHolder(View itemView) {
            super(itemView);
            tvSmsFrom = (TextView) itemView.findViewById(R.id.tvSmsFrom);
            tvSmsTo = (TextView) itemView.findViewById(R.id.tvSmsTo);
            tvSmsMsg = (TextView) itemView.findViewById(R.id.tvSmsMsg);
            tvSmsDateTime = (TextView) itemView.findViewById(R.id.tvSmsDateTime);
            lnContentItem = (LinearLayout) itemView.findViewById(R.id.lnContentItem);
            lnContentMsm = itemView.findViewById(R.id.lnContentMsm);
            tvMsgError = itemView.findViewById(R.id.tvMsgError);
        }
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }
    public interface OnLogItemClickListener{
        public void onItemLongClickListener(View view, int position);
    }

    public void SetOnItemClickListener(final OnItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }
    public void SetOnLogItemClickListener(OnLogItemClickListener onLogItemClickListener){
        this.logItemClickListener = onLogItemClickListener;
    }

}
