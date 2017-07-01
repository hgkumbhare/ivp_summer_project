a=1
for i in *.jpg; do
  new=$(printf "%d.jpg" "$a")
  mv -i -- "$i" "$new"
  a=$((a+1))
done
