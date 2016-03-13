package tv.esporter.lurkerstats;

import tv.esporter.lurkerstats.service.StatsItem;

/**
 * This interface must be implemented by activities that contain this
 * fragment to allow an interaction in this fragment to be communicated
 * to the activity and potentially other fragments contained in that
 * activity.
 * <p/>
 * See the Android Training lesson <a href=
 * "http://developer.android.com/training/basics/fragments/communicating.html"
 * >Communicating with Other Fragments</a> for more information.
 */
public interface OnStatsItemListFragmentInteractionListener {
    // TODO: Update argument type and name
    void onListFragmentInteraction(StatsItem item);
}
