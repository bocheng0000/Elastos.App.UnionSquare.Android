package org.elastos.wallet.ela.ui.crvote.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.elastos.wallet.R;
import org.elastos.wallet.ela.ElaWallet.MyWallet;
import org.elastos.wallet.ela.base.BaseFragment;
import org.elastos.wallet.ela.ui.crvote.bean.CRListBean;
import org.elastos.wallet.ela.utils.AppUtlis;
import org.elastos.wallet.ela.utils.Arith;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CRNodeCartAdapter extends RecyclerView.Adapter<CRNodeCartAdapter.MyViewHolder> {
    private List<CRListBean.DataBean.ResultBean.CrcandidatesinfoBean> list;
    private Context context;


    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    private BigDecimal balance;

    // 构造器
    public CRNodeCartAdapter(List<CRListBean.DataBean.ResultBean.CrcandidatesinfoBean> list, BaseFragment context) {
        this.context = context.getContext();
        this.list = list;
        // dataMap = new HashMap<Integer, Boolean>();
        // dataMapELA = new HashMap<Integer, BigDecimal>();
        // 初始化数据
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cr_nodcart, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        CRListBean.DataBean.ResultBean.CrcandidatesinfoBean producersBean = list.get(position);

        holder.checkbox.setChecked(producersBean.isChecked());


        holder.tvName.setText(producersBean.getNickname());
        int id = producersBean.getIndex() + 1;
        holder.tvId.setText(context.getString(R.string.currentrank) + id);
        // holder.tvZb.setText(context.getString(R.string.vote_of) + "：" + NumberiUtil.numberFormat(Double.parseDouble(producersBean.getVoterate()) * 100 + "", 5) + "%");
        holder.tvTicketnum.setText(context.getString(R.string.ticketnum) + producersBean.getVotes());
        holder.tvAddress.setText(AppUtlis.getLoc(context, producersBean.getLocation() + ""));
        holder.etTicketnum.setTag(false);
        if (holder.etTicketnum.getTag(R.id.et_ticketnum) != null) {
            holder.etTicketnum.removeTextChangedListener((TextWatcher) holder.etTicketnum.getTag(R.id.et_ticketnum));
        }
        if (producersBean.getCurentBalance() != null) {
            holder.etTicketnum.setText(producersBean.getCurentBalance().toPlainString());
        }
        addTextChangedListener(holder.etTicketnum, position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.checkbox.toggle();
                producersBean.setChecked(holder.checkbox.isChecked());
                if (onViewClickListener != null) {
                    onViewClickListener.onItemViewClick(CRNodeCartAdapter.this, v, position);
                }
            }
        });

    }


    @Override
    public int getItemCount() {
        return list.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.checkbox)
        AppCompatCheckBox checkbox;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_id)
        TextView tvId;
        @BindView(R.id.tv_zb)
        TextView tvZb;
        @BindView(R.id.tv_ticketnum)
        TextView tvTicketnum;
        @BindView(R.id.tv_address)
        TextView tvAddress;
        @BindView(R.id.et_ticketnum)
        EditText etTicketnum;


        MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);


        }
    }

    private OnViewClickListener onViewClickListener;

    public void setOnViewClickListener(OnViewClickListener onViewClickListener) {
        this.onViewClickListener = onViewClickListener;
    }

    public interface OnViewClickListener {
        void onItemViewClick(CRNodeCartAdapter adapter, View clickView, int position);
    }

    private OnTextChangedListener onTextChangedListener;

    public void setOnTextChangedListener(OnTextChangedListener onTextChangedListener) {
        this.onTextChangedListener = onTextChangedListener;
    }

    public interface OnTextChangedListener {
        void onTextChanged(CRNodeCartAdapter adapter, View clickView, int position);
    }

    public BigDecimal getCountEla() {
        BigDecimal sum = new BigDecimal(0);
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).isChecked() && list.get(i).getCurentBalance() != null) {
                    sum = sum.add(list.get(i).getCurentBalance());
                }
            }
        }
        return sum;
    }

    public int getCheckNum() {
        int sum = 0;
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).isChecked()) {
                    sum++;
                }
            }
        }
        return sum;
    }

    // 初始化dataMap的数据
    public void initDateStaus(boolean status) {
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                list.get(i).setChecked(status);
            }
        }
    }

    // 初始化dataMap的数据
    public void equalDataMapELA() {
        BigDecimal curentbalance = Arith.div(balance, list.size(), 8);
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                list.get(i).setCurentBalance(curentbalance);
            }
        }
    }

    // 初始化dataMap的数据
    public Map<String, String> getCheckedData() {
        Map<String, String> checkedData = new HashMap();
        if (list != null) {
            for (CRListBean.DataBean.ResultBean.CrcandidatesinfoBean bean : list) {
                if (bean.isChecked() && bean.getCurentBalance() != null
                        && !TextUtils.isEmpty(bean.getCurentBalance().toPlainString())) {
                    checkedData.put("\""+bean.getDid()+"\"", "\""+bean.getCurentBalance().multiply(new BigDecimal(MyWallet.RATE)).setScale(0, BigDecimal.ROUND_DOWN).toPlainString()+"\"");
                }

            }
        }
        return checkedData;
    }


    private void addTextChangedListener(EditText editText, int position) {

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (editText.getTag() != null && (boolean) editText.getTag()) {
                    editText.setTag(false);
                    return;
                }
                if (TextUtils.isEmpty(s)) {
                    return;
                }
                String number = s.toString().trim();
                if (new BigDecimal(number).compareTo(balance) > 0) {
                    number = balance.toPlainString();
                    editText.setTag(true);
                    editText.setText(number);
                } else if (number.split("\\.").length > 1 && number.split("\\.")[1].length() > 8) {
                    number = (number.split("\\."))[0] + "." + number.split("\\.")[1].substring(0, 8);
                    editText.setTag(true);
                    editText.setText(number);
                }

                list.get(position).setCurentBalance(new BigDecimal(number));
                if (onTextChangedListener != null) {
                    onTextChangedListener.onTextChanged(CRNodeCartAdapter.this, editText, position);
                }
            }
        };
        editText.addTextChangedListener(textWatcher);
        editText.setTag(R.id.et_ticketnum, textWatcher);
    }
}
