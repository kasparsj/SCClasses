+ SoundFile {
  *convertSampleRate {|path,newSampleRate,newHeaderFormat, newSampleFormat, chunkSize = 4194304|
    // TODO: Fine a way to do this without converting the data (ignore newSampleFormat)
    var s, t, d;
    s = SoundFile.openRead(path);
    if (s.isNil) {
      Error("Could not open file %".format(path)).throw;
    };
    t = SoundFile(path ++ ".s_convert");
    t.sampleFormat = newSampleFormat ?? s.sampleFormat;
    t.headerFormat = newHeaderFormat ?? s.headerFormat;
    t.sampleRate = newSampleRate ?? s.sampleRate;
    t.openWrite;
    d = FloatArray.newClear(chunkSize);
    while { d.size > 0 } {
      s.readData(d);
      t.writeData(d);
    };
    s.close;
    t.close;
    "mv % %".format(t.path, s.path).systemCmd;
  }
}