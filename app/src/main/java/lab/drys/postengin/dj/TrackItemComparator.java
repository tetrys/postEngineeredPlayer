package lab.drys.postengin.dj;

import java.util.Comparator;

/**
 * Created by lykanthrop on 1508/04/.
 */
public class TrackItemComparator implements Comparator<Integer>
{
	@Override
	public int compare(Integer lhs, Integer rhs)
	{
		return -lhs.compareTo(rhs);
	}
}
