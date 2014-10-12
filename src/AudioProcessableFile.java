
public interface AudioProcessableFile {
	
	/**
	 * readSamples: -> float[]
	 * @return : The samples of an audio file
	 */
	float[] readSamples();
	
	/**
	 * validateFile: -> void
	 * @effect: Validates the file to check if it is one of the saupported formats
	 */
	void validateFile();
	
	void compare(AudioProcessableFile ap);
	
	long getFileLength();
}
