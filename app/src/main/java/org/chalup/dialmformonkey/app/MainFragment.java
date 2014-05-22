package org.chalup.dialmformonkey.app;

import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import java.text.Collator;
import java.util.Locale;

public class MainFragment extends ListFragment {

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    getListView().setFastScrollEnabled(true);
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    setListAdapter(new Adapter(getActivity()));
  }

  @Override
  public void onListItemClick(ListView l, View v, int position, long id) {
    super.onListItemClick(l, v, position, id);

    Log.d("DMFM", "Clicked on item " + position + " which is " +
            (getListAdapter().isEnabled(position)
                ? "enabled"
                : "disabled")
    );
    Log.d("DMFM", getListAdapter().getItem(position).toString());
  }

  private static class Adapter extends BaseAdapter implements SectionIndexer {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private final LayoutInflater mInflater;
    private final SparseArray<String> mSections;
    private final String mItems[];

    public Adapter(Context context) {
      mInflater = LayoutInflater.from(context);
      mItems = Cheeses.CHEESES;
      mSections = calculateSections(mItems);
    }

    public SparseArray<String> calculateSections(String items[]) {
      final Collator collator = Collator.getInstance();
      final SparseArray<String> sections = new SparseArray<String>();

      String currentSection = null;
      for (int i = 0; i < items.length; i++) {
        String item = items[i];

        String section = item.substring(0, 2).toUpperCase(Locale.US);
        if (currentSection == null || collator.compare(section, currentSection) != 0) {
          sections.append(i + sections.size(), section);
          currentSection = section;
        }
      }

      return sections;
    }

    @Override
    public int getViewTypeCount() {
      return 2;
    }

    @Override
    public int getItemViewType(int position) {
      if (mSections.indexOfKey(position) >= 0) {
        return TYPE_HEADER;
      } else {
        return TYPE_ITEM;
      }
    }

    @Override
    public Object[] getSections() {
      String[] sections = new String[mSections.size()];
      for (int i = 0; i != mSections.size(); ++i) {
        sections[i] = mSections.valueAt(i);
      }
      return sections;
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
      return mSections.keyAt(sectionIndex);
    }

    @Override
    public int getSectionForPosition(int position) {
      for (int i = 1; i < mSections.size(); ++i) {
        if (position < mSections.keyAt(i)) {
          return i - 1;
        }
      }
      return mSections.size() - 1;
    }

    private int getCursorPosition(int position) {
      return position - (getSectionForPosition(position) + 1);
    }

    @Override
    public int getCount() {
      return mItems.length + mSections.size();
    }

    @Override
    public Object getItem(int position) {
      return getItemViewType(position) == TYPE_ITEM
          ? mItems[getCursorPosition(position)]
          : null;
    }

    @Override
    public long getItemId(int position) {
      return getItemViewType(position) == TYPE_ITEM
          ? getCursorPosition(position)
          : 0;
    }

    @Override
    public boolean areAllItemsEnabled() {
      return false;
    }

    @Override
    public boolean isEnabled(int position) {
      return getItemViewType(position) == TYPE_ITEM;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      if (getItemViewType(position) == TYPE_HEADER) {
        TextView header = convertView == null
            ? (TextView) mInflater.inflate(R.layout.list_section, parent, false)
            : (TextView) convertView;

        header.setText(mSections.valueAt(getSectionForPosition(position)));
        return header;
      } else {
        TextView item = convertView == null
            ? (TextView) mInflater.inflate(android.R.layout.simple_list_item_1, parent, false)
            : (TextView) convertView;

        item.setText(mItems[getCursorPosition(position)]);
        return item;
      }
    }
  }
}
