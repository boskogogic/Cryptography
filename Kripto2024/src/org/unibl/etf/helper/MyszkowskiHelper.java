package org.unibl.etf.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class MyszkowskiHelper {
	
	private String key;
	
	public MyszkowskiHelper(String k)
	{
		key=k;
	}
	

	
	public String encode(String plain)
	 {
		int[] num_in=generate_order(key);
		char[][] block=build_block(plain, -1,key.length(), 0);
		char[][] trans_block=transp_block(block,num_in);
		String cipher_text=read_block(trans_block);
		
		return cipher_text;
	 }
	
	private String read_block(char[][] trans_block)
	{
		int start_pos=0;
		int end_pos=0;
		//int row=trans_block.length;
		int col=trans_block[0].length;
		StringBuilder sb=new StringBuilder();
		while(end_pos<col)
		{
			if(trans_block[0][end_pos]==trans_block[0][start_pos])
			{
				end_pos++;
			}
			else
			{
				sb.append(read_col(trans_block,start_pos,end_pos-1));
				start_pos=end_pos;
			//	end_pos++;
			}
		}
		sb.append(read_col(trans_block,start_pos,end_pos-1));
		return sb.toString();
	}
	
	private String read_col(char[][] trans_block,int start_pos,int end_pos)
	{
		if(start_pos>end_pos)
			return "";
		StringBuilder sb=new StringBuilder();
		int row=trans_block.length;
		int col=trans_block[0].length;
		assert(end_pos<col);
		for(int i=1;i<row;i++)
		{
		 for(int j=start_pos;j<=end_pos;j++)
		 {
			 if(trans_block[i][j]!='\0')
			 {
			   sb.append(trans_block[i][j]);
		 
			 }
		 }
		}
			
		return sb.toString();
	}
	
	private char[][] transp_block(char[][] block, int[] num)
	{
		int row=block.length;
		int col=block[0].length;
		char[][] result=new char[row+1][col];
		int cur_num=1;
		int filled=0;
		int find_pos=0;
		while(filled<col)
		{
			int pos=search_index(num, cur_num, find_pos);
			if(pos==-1)
			{
				cur_num++;
				find_pos=0;
				continue;
			}
			else
			{
				result[0][filled]=(char)('0'+num[pos]);
				//copy column
				for(int i=0;i<row;i++)
				{
					result[i+1][filled]=block[i][pos];
				}
				filled++;
				find_pos=pos+1;
			}
		}
		return result;
	}
	
	
	/*
	 * Generate alphabetical order of the key, starting from 1.
	 * Same character has the same order (different from Cadenus.generate_order)
	 */
	public static int[] generate_order(String key)
	{
		String key_u=key.toUpperCase();
		char[] k_char=key_u.toCharArray();
		Arrays.sort(k_char);
		int[] result=new int[key.length()];
		int cur_index=1;
		int from_index=0;
		for(int i=0;i<key.length();i++)
		{
			if(i==0 || k_char[i]==k_char[i-1])
			{
				//key.
				int pos=key_u.indexOf(k_char[i],from_index);
				from_index=pos+1;
				result[pos]=cur_index;
			}
			else
			{
				cur_index++;
				from_index=0;
				int pos=key_u.indexOf(k_char[i],from_index);
				from_index=pos+1;
				result[pos]=cur_index;
			}
		}
		return result;
	}
	
	public static char[][] build_block(String plain, int row,int period, int direction)
	{
		String plain_u=plain.toUpperCase();
		if(row==-1)
		{
		   row=plain_u.length()/period;
		}
		if(plain_u.length()%period!=0)
		{
			row++;
		}
		char[][] result=new char[row][period];
		if(direction==1)//1 for vertical, 0 for horizontal
		{
			int cur_row=0;
			int cur_col=0;
			for(int i=0;i<plain_u.length();i++)
			{
				result[cur_row][cur_col]=plain_u.charAt(i);
				if(cur_row == row-1)
				{
					cur_row=0;
					cur_col++;
				}
				else
				{
					cur_row++;
				}
			}
		}
		else
		{
			int cur_row=0;
			int cur_col=0;
			for(int i=0;i<plain_u.length();i++)
			{
				result[cur_row][cur_col]=plain_u.charAt(i);
				if(cur_col == period-1)
				{
					cur_row++;
					cur_col=0;
				}
				else
				{
					cur_col++;
				}
			}
		}
		return result;
	}
	
	/*
	 * Generate a random permutation of [start,end)
	 */
	public static int search_index(int[] array,int num,int start_index)
	{
		for(int i=start_index;i<array.length;i++)
		{
			if(array[i]==num)
				return i;
		}
		return -1;
	}
}
