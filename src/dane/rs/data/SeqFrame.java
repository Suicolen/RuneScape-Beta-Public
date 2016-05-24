package dane.rs.data;

import dane.rs.io.*;
import dane.rs.world.scene.*;

public class SeqFrame {

	public static SeqFrame[] instance;

	public int delay;
	public SeqTransform transform;
	public int groupCount;
	public int[] groups;
	public int[] x;
	public int[] y;
	public int[] z;

	public static void load(Archive a) {
		Buffer head = new Buffer(a.get("frame_head.dat", null));
		Buffer tran1 = new Buffer(a.get("frame_tran1.dat", null));
		Buffer tran2 = new Buffer(a.get("frame_tran2.dat", null));
		Buffer delay = new Buffer(a.get("frame_del.dat", null));

		int frameCount = head.getUShort();
		int totalFrames = head.getUShort();

		instance = new SeqFrame[totalFrames + 1];

		int[] labels = new int[500];
		int[] x = new int[500];
		int[] y = new int[500];
		int[] z = new int[500];

		for (int frame = 0; frame < frameCount; frame++) {
			SeqFrame f = instance[head.getUShort()] = new SeqFrame();
			f.delay = delay.getUByte();

			SeqTransform t = SeqTransform.instance[head.getUShort()];
			f.transform = t;

			int groupCount = head.getUByte();
			int lastGroup = -1;
			int count = 0;

			for (int n = 0; n < groupCount; n++) {
				int flags = tran1.getUByte();

				if (flags > 0) {
					if (t.types[n] != Model.TYPE_BONE) {
						for (int group = n - 1; group > lastGroup; group--) {
							if (t.types[group] == Model.TYPE_BONE) {
								labels[count] = group;
								x[count] = 0;
								y[count] = 0;
								z[count] = 0;
								count++;
								break;
							}
						}
					}

					labels[count] = n;

					int defaultValue = 0;

					if (t.types[labels[count]] == Model.TYPE_SCALE) {
						defaultValue = 128;
					}

					if ((flags & 0x1) != 0) {
						x[count] = tran2.getSmart();
					} else {
						x[count] = defaultValue;
					}

					if ((flags & 0x2) != 0) {
						y[count] = tran2.getSmart();
					} else {
						y[count] = defaultValue;
					}

					if ((flags & 0x4) != 0) {
						z[count] = tran2.getSmart();
					} else {
						z[count] = defaultValue;
					}

					lastGroup = n;
					count++;
				}
			}

			f.groupCount = count;
			f.groups = new int[count];
			f.x = new int[count];
			f.y = new int[count];
			f.z = new int[count];

			for (int j = 0; j < count; j++) {
				f.groups[j] = labels[j];
				f.x[j] = x[j];
				f.y[j] = y[j];
				f.z[j] = z[j];
			}
		}
	}
}
